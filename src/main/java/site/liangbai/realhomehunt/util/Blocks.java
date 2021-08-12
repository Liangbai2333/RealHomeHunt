/*
 * RealHomeHunt
 * Copyright (C) 2021  Liangbai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package site.liangbai.realhomehunt.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.PacketPlayOutBlockBreakAnimation;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.Location;
import org.bukkit.Nameable;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.block.data.type.Door;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.Lootable;
import site.liangbai.realhomehunt.api.residence.Residence;
import site.liangbai.realhomehunt.common.config.Config;
import site.liangbai.realhomehunt.internal.task.NaturallyDropItemTask;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Blocks {
    public static final Map<Container, Inventory> CACHED_INVENTORY = new ConcurrentHashMap<>();

    private static int id;

    public static int nextId() {
        if (id >= Integer.MAX_VALUE) id = 0;

        return ++id;
    }

    public static void sendBreakBlockPacket(Block block, boolean dropItem) {
        World world = block.getWorld();

        Location location = block.getLocation();

        CraftWorld craftWorld = ((CraftWorld) world);

        WorldServer worldServer = craftWorld.getHandle();

        BlockPosition blockPosition = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        worldServer.b(blockPosition, dropItem);
    }

    public static void sendBreakAnimationPacket(int id, Block block, int breakSit) {
        Location location = block.getLocation();

        BlockPosition blockPosition = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(id, blockPosition, breakSit);

        Packets.sendPacketToAll(packet, location.getWorld());
    }

    public static void sendClearBreakAnimationPacket(int id, Block block) {
        sendBreakAnimationPacket(id, block, -1);
    }

    public static boolean isDoor(Block block) {
        return block.getBlockData() instanceof Door;
    }

    public static long containsBlockAndReturnCount(Config.BlockSetting.BlockIgnoreSetting.IgnoreBlockInfo info, Residence residence) {
        return Locations.getRegionBlocks(residence.getLeft(), residence.getRight()).stream()
                .parallel()
                .map(Block::getType)
                .filter(it -> !it.isAir())
                .filter(it -> it.name().equalsIgnoreCase(info.full) ||
                        (info.prefix != null && it.name().startsWith(info.prefix)) ||
                        (info.suffix != null && it.name().endsWith(info.suffix))
                )
                .count();
    }

    public static void applyPlaceBlock(Block block) {
        BlockData data = block.getBlockData().clone();

        if (data instanceof MultipleFacing) {
            MultipleFacing multipleFacing = ((MultipleFacing) data);

            multipleFacing.getAllowedFaces().forEach(blockFace -> {
                if (canConnected(block, blockFace)) {
                    multipleFacing.setFace(blockFace, true);
                }
            });
        }

        block.setBlockData(data);
    }

    private static boolean canConnected(Block current, BlockFace outside) {
        Block outsideBlock = current.getRelative(outside);

        BlockData outData = outsideBlock.getBlockData();

        BlockData currentData = current.getBlockData();

        return outData.getClass().equals(currentData.getClass()) && outData instanceof MultipleFacing && !((MultipleFacing) currentData).hasFace(outside);
    }

    public static void applyBlockState(BlockState newState, BlockState oldState) {
        if (oldState instanceof Container && Config.robChestMode.enabled && Config.robChestMode.fixItem) {
            Container container = ((Container) newState);
            Container snapshot = ((Container) oldState);

            Inventory inventory = container.getInventory();
            Inventory snapshotInventory = CACHED_INVENTORY.containsKey(snapshot) ? CACHED_INVENTORY.get(snapshot) : snapshot.getSnapshotInventory();

            CACHED_INVENTORY.remove(snapshot);

            List<ItemStack> list = InventoryHelper.copyTo(inventory, snapshotInventory);

            if (!list.isEmpty()) {
                NaturallyDropItemTask.setup(list, newState.getLocation());
            }
        } else if (oldState instanceof Sign) {
            Sign sign = ((Sign) newState);
            Sign snapshot = ((Sign) oldState);

            String[] lines = snapshot.getLines();

            sign.setEditable(snapshot.isEditable());
            sign.setColor(snapshot.getColor());

            for (int i = 0; i < lines.length; i++) {
                sign.setLine(i, lines[i]);
            }
        } else if (oldState instanceof CommandBlock) {
            CommandBlock commandBlock = ((CommandBlock) newState);
            CommandBlock snapshot = ((CommandBlock) oldState);

            commandBlock.setCommand(snapshot.getCommand());
            commandBlock.setName(snapshot.getName());
        } else if (oldState instanceof Skull) {
            Skull skull = ((Skull) newState);
            Skull snapshot = ((Skull) oldState);

            OfflinePlayer player = snapshot.getOwningPlayer();

            if (player != null) {
                skull.setOwningPlayer(player);
            }

            PlayerProfile profile = snapshot.getPlayerProfile();

            if (profile != null) {
                skull.setPlayerProfile(profile);
            }
        }

        if (oldState instanceof Lockable) {
            ((Lockable) newState).setLock(((Lockable) oldState).getLock());
        }

        if (oldState instanceof Nameable) {
            ((Nameable) newState).setCustomName(((Nameable) oldState).getCustomName());
        }

        if (oldState instanceof Lootable) {
            Lootable lootable = ((Lootable) newState);
            Lootable snapshot = ((Lootable) oldState);

            lootable.setLootTable(snapshot.getLootTable());
            lootable.setSeed(snapshot.getSeed());
        }
    }
}

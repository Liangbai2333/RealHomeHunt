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

package site.liangbai.realhomehunt.task;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Nameable;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.loot.Lootable;
import org.bukkit.scheduler.BukkitRunnable;
import site.liangbai.realhomehunt.RealHomeHunt;
import site.liangbai.realhomehunt.api.residence.Residence;
import site.liangbai.realhomehunt.config.Config;

/**
 * The type Auto fix block task.
 *
 * @author Liangbai
 * @since 2021 /08/10 11:32 上午
 */
public final class AutoFixBlockTask extends BukkitRunnable {
    private final Residence residence;
    private final BlockState blockState;
    private final BlockData blockData;
    private final Location location;

    public AutoFixBlockTask(Residence residence, BlockState blockState, BlockData blockData, Location location) {
        this.residence = residence;
        this.blockState = blockState;
        this.blockData = blockData;
        this.location = location;
    }

    @Override
    public void run() {
        if (Config.autoFixResidence.ignoreEnemy || !residence.hasEnemyIn()) {
            Block block = location.getBlock();

            if (needFixed(block)) {
                block.setBlockData(blockData);

                block.getState().update(true, true);

                applyBlockState(block.getState(), blockState);
            }
        } else
            submit(residence, blockState, blockData, location);
    }

    private static boolean needFixed(Block block) {
        Material type = block.getType();

        return type.isAir() || (!type.isSolid() && !type.isItem());
    }

    private static void applyBlockState(BlockState newState, BlockState oldState) {
        if (oldState instanceof Container && Config.robChestMode.fixItem) {
            Container container = ((Container) newState);
            Container snapshot = ((Container) oldState);

            container.getInventory().setContents(snapshot.getSnapshotInventory().getContents());
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

    public static void submit(Residence residence, BlockState snapshotState, BlockData blockData, Location location) {
        new AutoFixBlockTask(residence, snapshotState, blockData, location).runTaskLater(RealHomeHunt.getInst(), Config.autoFixResidence.perBlockFixedMills);
    }
}

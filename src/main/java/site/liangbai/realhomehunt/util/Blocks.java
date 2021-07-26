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

import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.PacketPlayOutBlockBreakAnimation;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import site.liangbai.realhomehunt.config.Config;
import site.liangbai.realhomehunt.residence.Residence;

public final class Blocks {
    private static int id;

    public static int nextId() {
        if (id >= Integer.MAX_VALUE) id = 0;

        return ++id;
    }

    public static void sendBreakBlockPacket(Block block) {
        World world = block.getWorld();

        Location location = block.getLocation();

        CraftWorld craftWorld = ((CraftWorld) world);

        WorldServer worldServer = craftWorld.getHandle();

        BlockPosition blockPosition = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        worldServer.b(blockPosition, true);
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

    public static int containsBlockAndReturnCount(Config.BlockSetting.BlockIgnoreSetting.IgnoreBlockInfo info, Residence residence) {
        Locations.LocationSortInfo sortInfo = Locations.sort(residence.getLeft(), residence.getRight());

        Location min = sortInfo.getMin();

        Location max = sortInfo.getMax();

        int count = 0;

        for (int x = min.getBlockX(); x < max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y < max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z < max.getBlockZ(); z++) {
                    Location blockLocation = new Location(min.getWorld(), x, y, z);

                    Block block = blockLocation.getBlock();

                    if (block.getType() == Material.AIR) continue;

                    String name = block.getType().name().toUpperCase();

                    if (info.full != null && !name.equalsIgnoreCase(info.full)) continue;

                    if (!name.startsWith(info.prefix) || !name.endsWith(info.suffix)) continue;

                    count++;
                }
            }
        }

        return count;
    }
}
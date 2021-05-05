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

import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public final class Packets {
    public static void sendPacket(Player player, Packet<?> packet) {
        if (!(player instanceof CraftPlayer)) return;

        CraftPlayer craftPlayer = ((CraftPlayer) player);

        EntityPlayer entityPlayer = craftPlayer.getHandle();

        PlayerConnection playerConnection = entityPlayer.playerConnection;

        playerConnection.sendPacket(packet);
    }

    public static void sendPacketToAll(Packet<?> packet, World world) {
        Bukkit.getOnlinePlayers().stream()
                .filter(it -> it.getWorld().equals(world))
                .forEach(it -> sendPacket(it, packet));
    }
}

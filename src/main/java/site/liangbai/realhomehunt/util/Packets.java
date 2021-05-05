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

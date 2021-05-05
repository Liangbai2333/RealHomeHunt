package site.liangbai.realhomehunt.util;

import net.minecraft.server.v1_16_R3.ChatMessageType;
import net.minecraft.server.v1_16_R3.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;

public final class ActionBar {
    public static void sendActionBar(Player player, String message) {
        PacketPlayOutChat packet = new PacketPlayOutChat(CraftChatMessage.fromStringOrNull(message), ChatMessageType.GAME_INFO, player.getUniqueId());

        Packets.sendPacket(player, packet);
    }
}

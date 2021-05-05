package site.liangbai.realhomehunt.util;

import org.bukkit.Bukkit;
import site.liangbai.realhomehunt.config.Config;

public final class Console {
    public static void sendMessage(String message) {
        Bukkit.getConsoleSender().sendMessage(message);
    }

    public static void sendMessage(String message, String prefix) {
        sendMessage(prefix + message);
    }

    public static void sendMessage(String message, boolean prefix) {
        if (prefix) {
            sendMessage(message, Config.prefix);
        } else sendMessage(message);
    }

    public static void sendRawMessage(String message) {
        sendMessage(message, true);
    }
}

package site.liangbai.realhomehunt.util;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import site.liangbai.realhomehunt.locale.impl.Locale;
import site.liangbai.realhomehunt.locale.manager.LocaleManager;

public final class MessageUtil {
    public static void sendToAll(String node, Object... args) {
        Bukkit.getOnlinePlayers().forEach(it -> {
            Locale locale = LocaleManager.require(it);

            it.sendMessage(locale.asString(node, args));
        });

        ConsoleCommandSender sender = Bukkit.getConsoleSender();

        Locale locale = LocaleUtil.require(sender);

        sender.sendMessage(locale.asString(node, args));
    }
}

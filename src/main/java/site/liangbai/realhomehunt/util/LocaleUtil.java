package site.liangbai.realhomehunt.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import site.liangbai.realhomehunt.locale.impl.Locale;
import site.liangbai.realhomehunt.locale.manager.LocaleManager;

public final class LocaleUtil {
    public static Locale require(CommandSender sender) {
        return sender instanceof Player ? LocaleManager.require(((Player) sender)) : LocaleManager.requireDefault();
    }
}

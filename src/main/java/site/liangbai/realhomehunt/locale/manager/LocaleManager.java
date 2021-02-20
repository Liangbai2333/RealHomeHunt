package site.liangbai.realhomehunt.locale.manager;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import site.liangbai.realhomehunt.config.Config;
import site.liangbai.realhomehunt.locale.impl.Locale;
import site.liangbai.realhomehunt.util.ConsoleUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class LocaleManager {
    private static final Map<String, Locale> locales = new HashMap<>();

    public static void init(Plugin plugin) {
        locales.clear();

        ConsoleUtil.sendRawMessage(ChatColor.GREEN + "Loading language setting...");

        File langFolder = new File(plugin.getDataFolder(), "lang");

        if (!langFolder.exists()) {
            if (!langFolder.mkdirs()) throw new IllegalStateException("can not create new folder: lang");

            saveDefaultLocale(plugin);
        }

        File[] langFiles = langFolder.listFiles();

        if (langFiles != null) {
            for (File langFile : langFiles) {
                register(langFile);
            }
        }

        ConsoleUtil.sendRawMessage(ChatColor.GREEN + "Loading language successful.");

        ConsoleUtil.sendRawMessage(ChatColor.GREEN + "Locales: " + locales.keySet());
    }

    public static void register(@NotNull File file) {
        String fileName = file.getName();

        String name = fileName.substring(0, fileName.lastIndexOf("."));

        locales.put(name, new Locale(YamlConfiguration.loadConfiguration(file)));
    }

    public static void saveDefaultLocale(Plugin plugin) {
        plugin.saveResource("lang/zh_cn.yml", false);
    }

    public static String getDefaultLocale() {
        return "zh_cn";
    }

    public static Locale requireDefault() {
        String localeName = Config.consoleLanguage;

        if (!locales.containsKey(localeName)) {
            return locales.get(getDefaultLocale());
        }

        return locales.get(localeName);
    }

    @NotNull
    public static Locale require(Player player) {
        String localeName = player.getLocale();

        if (!locales.containsKey(localeName)) {
            return locales.get(getDefaultLocale());
        }

        return locales.get(localeName);
    }
}

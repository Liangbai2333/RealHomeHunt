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

package site.liangbai.realhomehunt.api.locale.manager;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import site.liangbai.realhomehunt.common.config.Config;
import site.liangbai.realhomehunt.api.locale.impl.Locale;
import site.liangbai.realhomehunt.util.Console;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class LocaleManager {
    private static final Map<String, Locale> locales = new HashMap<>();

    public static void init(Plugin plugin) {
        locales.clear();

        Console.sendRawMessage(ChatColor.GREEN + "Loading language setting...");

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

        Console.sendRawMessage(ChatColor.GREEN + "Loading language successful.");

        Console.sendRawMessage(ChatColor.GREEN + "Locales: " + locales.keySet());
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

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

package site.liangbai.realhomehunt.api.locale.impl;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import site.liangbai.realhomehunt.common.config.Config;
import site.liangbai.realhomehunt.api.locale.ILocale;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Locale.
 *
 * @author Liangbai
 * @since 2021 /08/10 01:55 下午
 */
public final class Locale implements ILocale {
    private final Map<String, String> nodes = new HashMap<>();

    public Locale(ConfigurationSection section) {
        section.getKeys(true).stream()
                .filter(it -> section.isString(it) || section.isList(it))
                .forEach(it -> {
                    Object object = section.get(it);

                    if (object instanceof String) {
                        nodes.put(it, translateColoredString((String) object));
                    } else if (object instanceof List) {
                        List<String> list = section.getStringList(it);

                        StringBuilder stringBuilder = new StringBuilder();

                        list.forEach(s -> stringBuilder.append(translateColoredString(s)).append("\n"));

                        nodes.put(it, stringBuilder.toString());
                    }
                });
    }

    private String translateColoredString(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    @NotNull
    @Override
    public String asString(String node, Object... args) {
        if (!nodes.containsKey(node)) return "(" + node + ")";

        String result = nodes.get(node);

        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];

            if (arg != null) {
                result = result.replace("{" + i + "}", arg.toString());
            }
        }

        result = result.replace("{prefix}", Config.prefix);

        return result;
    }
}

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

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import site.liangbai.realhomehunt.locale.impl.Locale;
import site.liangbai.realhomehunt.locale.manager.LocaleManager;

public final class Messages {
    public static void sendToAll(String node, Object... args) {
        Bukkit.getOnlinePlayers().forEach(it -> {
            Locale locale = LocaleManager.require(it);

            it.sendMessage(locale.asString(node, args));
        });

        ConsoleCommandSender sender = Bukkit.getConsoleSender();

        Locale locale = Locales.require(sender);

        sender.sendMessage(locale.asString(node, args));
    }
}

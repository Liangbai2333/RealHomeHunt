/*
 * RealHomeHunt
 * Copyright (C) 2022  Liangbai
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

package site.liangbai.realhomehunt.util

import org.bukkit.Bukkit
import site.liangbai.realhomehunt.common.config.Config

object Console {
    fun sendMessage(message: String?) {
        Bukkit.getConsoleSender().sendMessage(message)
    }

    fun sendMessage(message: String, prefix: String) {
        sendMessage(prefix + message)
    }

    fun sendMessage(message: String, prefix: Boolean) {
        if (prefix) {
            sendMessage(message, Config.prefix)
        } else sendMessage(message)
    }

    fun sendRawMessage(message: String) {
        sendMessage(message, true)
    }
}

fun pluginInfo(message: String) = Console.sendRawMessage(message)
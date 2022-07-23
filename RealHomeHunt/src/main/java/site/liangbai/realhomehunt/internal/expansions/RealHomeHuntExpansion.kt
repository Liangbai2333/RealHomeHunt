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

package site.liangbai.realhomehunt.internal.expansions

import org.bukkit.ChatColor
import org.bukkit.entity.Player
import site.liangbai.realhomehunt.api.residence.attribute.getName
import site.liangbai.realhomehunt.api.residence.attribute.map.AttributeMap
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager
import site.liangbai.realhomehunt.internal.command.Command
import site.liangbai.realhomehunt.util.Console
import taboolib.common.platform.function.pluginId
import taboolib.platform.compat.PlaceholderExpansion

object RealHomeHuntExpansion : PlaceholderExpansion {
    init {
        Console.sendMessage("${ChatColor.YELLOW}Registering to PlaceholderAPI in identifier: $identifier.")
    }

    override val identifier: String
        get() = pluginId.lowercase()

    override fun onPlaceholderRequest(player: Player?, args: String): String {
        val split = args.split("_")
        if (split.size <= 1) return "Unknown"
        val owner = split[0]
        if (player == null) return "Unknown"
        val residence = ResidenceManager.getResidenceByOwner(owner) ?: return "Unknown"
        return when(split[1]) {
            "attribute" -> {
                if (split.size <= 3) {
                    return "Unknown"
                }

                val attribute = AttributeMap.getMap(split[2])?.let { residence.getAttributeWithoutType(it) } ?: return "Unknown"
                return when (split[3]) {
                    "state" -> attribute.get().toString()
                    "name" -> attribute.getName(player)
                    else -> "Unknown"
                }
            }
            "show" -> (Command.showCaches[owner]?.contains(player.name) ?: false).toString()
            "administrator" -> {
                if (split.size <= 2) {
                    return "Unknown"
                }

                return when(split[2]) {
                    "list" -> residence.administrators.toString()
                    "size" -> residence.administrators.size.toString()
                    else -> {
                        if (residence.administrators.isEmpty()) {
                            return "Unknown"
                        } else residence.administrators[split[2].toInt()]
                    }
                }
            }
            else -> "Unknown"
        }
    }
}
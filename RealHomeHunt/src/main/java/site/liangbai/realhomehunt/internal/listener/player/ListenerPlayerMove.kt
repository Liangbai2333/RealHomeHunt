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

package site.liangbai.realhomehunt.internal.listener.player

import org.bukkit.event.player.PlayerMoveEvent
import site.liangbai.realhomehunt.util.Players
import taboolib.common.platform.event.SubscribeEvent

internal object ListenerPlayerMove {
    @SubscribeEvent
    fun onPlayerMove(event: PlayerMoveEvent) {
        val loc1 = event.from
        val loc2 = event.to
        if (loc1.x != loc2!!.x || loc1.z != loc2.z) {
            Players.push(event.player)
        }
    }
}
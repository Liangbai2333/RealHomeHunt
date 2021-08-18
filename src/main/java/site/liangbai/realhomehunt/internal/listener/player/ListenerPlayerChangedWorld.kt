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

package site.liangbai.realhomehunt.internal.listener.player

import org.bukkit.event.player.PlayerChangedWorldEvent
import site.liangbai.realhomehunt.api.cache.SelectCache
import site.liangbai.realhomehunt.common.particle.EffectGroup
import site.liangbai.realhomehunt.internal.command.Command
import taboolib.common.platform.event.SubscribeEvent

internal object ListenerPlayerChangedWorld {
    @SubscribeEvent
    fun onPlayerChangedWorld(event: PlayerChangedWorldEvent) {
        SelectCache.pop(event.player)
        val name = event.player.name
        if (Command.showCaches.containsKey(name)) {
            val effectGroup: EffectGroup? = Command.showCaches[name]
            effectGroup?.turnOff()
            Command.showCaches.remove(name)
        }
    }
}
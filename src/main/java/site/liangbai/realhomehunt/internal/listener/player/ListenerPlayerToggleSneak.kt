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

import org.bukkit.event.player.PlayerToggleSneakEvent
import site.liangbai.realhomehunt.api.locale.manager.LocaleManager
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager
import site.liangbai.realhomehunt.common.config.Config
import taboolib.common.platform.event.SubscribeEvent

internal object ListenerPlayerToggleSneak {
    @SubscribeEvent
    fun onPlayerToggleSneak(event: PlayerToggleSneakEvent) {
        if (!Config.residence.openWarn) return
        val player = event.player
        if (!ResidenceManager.isOpened(player.world)) return
        val location = player.location
        if (location.pitch > -80 || !event.isSneaking) {
            return
        }
        val residence = ResidenceManager.getResidenceByLocation(location)
        val locale = LocaleManager.require(player)
        if (residence == null) {
            player.sendMessage(locale.asString("action.warn.haveNotResidence"))
            return
        }
        if (!residence.isAdministrator(player)) {
            player.sendMessage(locale.asString("action.warn.isNotAdministrator"))
            return
        }
        if (!residence.isCanWarn) {
            player.sendMessage(locale.asString("action.warn.waitMessage"))
            return
        }
        residence.warn(player)
    }
}
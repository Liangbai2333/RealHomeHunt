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

package site.liangbai.realhomehunt.internal.listener.entity

import org.bukkit.entity.Player
import org.bukkit.event.hanging.HangingBreakByEntityEvent
import site.liangbai.realhomehunt.api.residence.attribute.impl.Break
import site.liangbai.realhomehunt.api.residence.attribute.impl.Build
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager
import taboolib.common.platform.event.SubscribeEvent

internal object ListenerHangingBreakByEntity {
    @SubscribeEvent
    fun onHangingBreakByEntity(event: HangingBreakByEntityEvent) {
        if (event.remover !is Player) return
        val player = event.remover as Player?
        if (!ResidenceManager.isOpened(player!!.world)) return
        val residence = ResidenceManager.getResidenceByLocation(event.entity.location)
            ?: return
        if (!residence.isAdministrator(player) && !player.hasPermission("rh.break")
            && !residence.checkBooleanAttribute<Break>() && !residence.checkBooleanAttribute<Build>()
        ) {
            event.isCancelled = true
        }
    }
}
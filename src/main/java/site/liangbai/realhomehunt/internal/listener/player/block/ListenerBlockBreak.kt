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

package site.liangbai.realhomehunt.internal.listener.player.block

import org.bukkit.event.block.BlockBreakEvent
import site.liangbai.realhomehunt.api.residence.attribute.impl.BreakAttribute
import site.liangbai.realhomehunt.api.residence.attribute.impl.BuildAttribute
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager
import taboolib.common.platform.event.SubscribeEvent

internal object ListenerBlockBreak {
    @SubscribeEvent
    fun onBlockBreak(event: BlockBreakEvent) {
        if (!ResidenceManager.isOpened(event.player.world)) return
        val residence = ResidenceManager.getResidenceByLocation(event.block.location)
            ?: return
        if (!residence.isAdministrator(event.player) && !event.player.hasPermission("rh.break")
            && !residence.checkBooleanAttribute(BreakAttribute::class.java) && !residence.checkBooleanAttribute(
                BuildAttribute::class.java
            )
        ) {
            event.isCancelled = true
            return
        }
        residence.destroyBlock(event.block)
    }
}
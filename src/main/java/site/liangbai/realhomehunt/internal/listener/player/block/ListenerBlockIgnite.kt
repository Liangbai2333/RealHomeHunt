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

import org.bukkit.Material
import org.bukkit.event.block.BlockPlaceEvent
import site.liangbai.realhomehunt.api.residence.attribute.impl.Ignite
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager
import taboolib.common.platform.event.SubscribeEvent

internal object ListenerBlockIgnite {
    @SubscribeEvent
    fun onBlockPlace(event: BlockPlaceEvent) {
        val block = event.block
        if (block.type != Material.FIRE) return
        if (!ResidenceManager.isOpened(block.world)) return
        val residence = ResidenceManager.getResidenceByLocation(block.location)
        if (residence != null) {
            if (!residence.checkBooleanAttribute<Ignite>()) event.isCancelled = true
        }
    }
}
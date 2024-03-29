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

package site.liangbai.realhomehunt.internal.listener.entity

import org.bukkit.event.entity.EntityExplodeEvent
import site.liangbai.realhomehunt.api.residence.attribute.impl.Explode
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager
import taboolib.common.platform.event.SubscribeEvent

internal object ListenerEntityExplode {
    @SubscribeEvent
    fun onEntityExplode(event: EntityExplodeEvent) {
        if (!ResidenceManager.isOpened(event.entity.world)) return
        val blocks = event.blockList()
        blocks
            .filterNotNull()
            .filter { !it.type.isAir }
            .filter {
                val residence = ResidenceManager.getResidenceByLocation(it.location)
                residence != null && !residence.checkBooleanAttribute<Explode>()
            }
            .forEach { blocks.remove(it) }
    }
}
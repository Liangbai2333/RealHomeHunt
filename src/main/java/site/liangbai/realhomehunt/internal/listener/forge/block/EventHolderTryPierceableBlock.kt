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

package site.liangbai.realhomehunt.internal.listener.forge.block

import site.liangbai.realhomehunt.util.kt.toBukkitWorld
import site.liangbai.realhomehunt.util.kt.toLocation
import taboolib.common.Isolated
import site.liangbai.forgeeventbridge.event.EventHolder
import site.liangbai.forgeeventbridge.wrapper.EventWrapper
import site.liangbai.realhomehunt.common.config.Config
import site.liangbai.realhomehuntforge.event.BlockRayTraceEvent.TryPierceableBlock

@Isolated
class EventHolderTryPierceableBlock : EventHolder<EventWrapper.EventObject> {
    override fun handle(eventWrapper: EventWrapper<EventWrapper.EventObject>) {
        val event = eventWrapper.getObject() as TryPierceableBlock
        val world = event.level.toBukkitWorld()
        val block = world.getBlockAt(event.rayTraceResult.blockPos.toLocation())
        val original = event.isPierceable
        event.isPierceable = Config.block.ignore.isPierceable(block.type, original)
    }
}
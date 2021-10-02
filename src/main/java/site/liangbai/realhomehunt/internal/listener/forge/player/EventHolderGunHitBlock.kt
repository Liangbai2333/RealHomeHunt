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

package site.liangbai.realhomehunt.internal.listener.forge.player

import site.liangbai.realhomehunt.util.kt.toBukkitEntity
import site.liangbai.realhomehunt.util.kt.toBukkitItemStack
import site.liangbai.realhomehunt.util.kt.toBukkitWorld
import site.liangbai.realhomehunt.util.kt.toLocation
import taboolib.common.Isolated
import site.liangbai.forgeeventbridge.event.EventHolder
import site.liangbai.forgeeventbridge.wrapper.EventWrapper
import com.craftingdead.core.event.GunEvent.HitBlock
import org.bukkit.entity.Player
import site.liangbai.realhomehunt.internal.processor.Processors

/**
 * The type Event holder gun hit block.
 *
 * @author Liangbai
 * @since 2021 /08/11 02:37 下午
 */
@Isolated
class EventHolderGunHitBlock : EventHolder<EventWrapper.EventObject> {
    override fun handle(eventWrapper: EventWrapper<EventWrapper.EventObject>) {
        val event = eventWrapper.getObject() as HitBlock
        val entity = event.living.entity.toBukkitEntity() as? Player ?: return
        val gun = event.itemStack.toBukkitItemStack()
        val world = event.level.toBukkitWorld()
        val block = world.getBlockAt(event.rayTraceResult.blockPos.toLocation())
        Processors.GUN_HIT_BLOCK_PROCESSOR.processGunHitBlock(entity, gun, block)
    }
}
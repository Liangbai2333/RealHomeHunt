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

package site.liangbai.realhomehunt.internal.listener.forge.arclight

import net.minecraftforge.eventbus.api.SubscribeEvent
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager
import site.liangbai.realhomehunt.util.kt.toBukkitWorld
import site.liangbai.realhomehunt.util.kt.toLocation
import site.liangbai.realhomehuntforge.event.BlockDestroyEvent
import taboolib.common.Isolated
import taboolib.common.reflect.Reflex.Companion.getProperty

@Isolated
class EventHandlerBlockDestroy {
    @SubscribeEvent
    fun onBlockDestroy(event: BlockDestroyEvent) {
        val world = event.getProperty<Any>("world")!!.toBukkitWorld()
        val location = event.pos.toLocation()
        val block = world.getBlockAt(event.pos.toLocation())
        val residence = ResidenceManager.getResidenceByLocation(location)
        if (residence != null) {
            residence.destroyBlock(block)
        }
    }
}
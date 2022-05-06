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

import net.minecraft.core.BlockPos
import net.minecraftforge.eventbus.api.SubscribeEvent
import site.liangbai.realhomehunt.common.config.Config
import site.liangbai.realhomehunt.util.kt.toBukkitWorld
import site.liangbai.realhomehunt.util.kt.toLocation
import site.liangbai.realhomehuntforge.event.BlockRayTraceEvent
import taboolib.common.Isolated
import taboolib.common.reflect.Reflex.Companion.getProperty

@Isolated
class EventHandlerTryPierceableBlock {
    @SubscribeEvent
    fun onTryPiecreableBlock(event: BlockRayTraceEvent.TryPierceableBlock) {
        val world = event.getProperty<Any>("level")!!.toBukkitWorld()
        val pos = event.getProperty<Any>("rayTraceResult")?.getProperty<BlockPos>("blockPos")
        val block = world.getBlockAt(pos!!.toLocation())
        val original = event.isPierceable
        event.isPierceable =
            Config.block.ignore.isPierceable(block.type, original)
    }
}
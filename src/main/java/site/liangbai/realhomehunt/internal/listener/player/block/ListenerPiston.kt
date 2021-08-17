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

import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.block.BlockPistonRetractEvent
import site.liangbai.realhomehunt.api.residence.attribute.impl.PistonAttribute
import site.liangbai.realhomehunt.api.residence.attribute.impl.PistonProtectionAttribute
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager
import taboolib.common.platform.event.SubscribeEvent

/**
 * The type Listener piston.
 *
 * @author Liangbai
 * @since 2021 /08/10 01:24 下午
 */
internal object ListenerPiston {
    @SubscribeEvent
    fun onPistonExtend(event: BlockPistonExtendEvent) {
        if (!ResidenceManager.isOpened(event.block.world)) return
        val residence = ResidenceManager.getResidenceByLocation(event.block.location)
        if (residence != null && !residence.checkBooleanAttribute(PistonAttribute::class.java)) {
            event.isCancelled = true
        }
        for (block in event.blocks) {
            val location = block.location.clone()
            val from = ResidenceManager.getResidenceByLocation(location)
            val direction = event.direction
            val to = ResidenceManager.getResidenceByLocation(
                location.clone().add(direction.modX.toDouble(), direction.modY.toDouble(), direction.modZ.toDouble())
            )
            if (from != null && from.checkBooleanAttribute(PistonProtectionAttribute::class.java)
                || to != null && to.checkBooleanAttribute(PistonProtectionAttribute::class.java)
            ) {
                event.isCancelled = true
                return
            }
        }
    }

    @SubscribeEvent
    fun onPistonRetract(event: BlockPistonRetractEvent) {
        if (!ResidenceManager.isOpened(event.block.world)) return
        var residence = ResidenceManager.getResidenceByLocation(event.block.location)
        if (residence != null && !residence.checkBooleanAttribute(PistonAttribute::class.java)) {
            event.isCancelled = true
        }
        if (event.isSticky) {
            for (block in event.blocks) {
                residence = ResidenceManager.getResidenceByLocation(block.location.clone())
                if (residence != null && residence.checkBooleanAttribute(PistonProtectionAttribute::class.java)) {
                    event.isCancelled = true
                    return
                }
            }
        }
    }
}
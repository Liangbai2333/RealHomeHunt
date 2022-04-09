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

package site.liangbai.realhomehunt.internal.listener.player.block

import org.bukkit.event.block.BlockMultiPlaceEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.inventory.ItemStack
import site.liangbai.realhomehunt.api.residence.attribute.impl.Build
import site.liangbai.realhomehunt.api.residence.attribute.impl.Place
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager
import site.liangbai.realhomehunt.common.config.Config
import site.liangbai.realhomehunt.util.sendLang
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.nms.i18n.I18n

internal object ListenerBlockPlace {
    @SubscribeEvent
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (!ResidenceManager.isOpened(event.player.world)) return
        val residence = ResidenceManager.getResidenceByLocation(event.block.location)
            ?: return
        if (!residence.isAdministrator(event.player) && !event.player.hasPermission("rh.place")
            && !residence.checkBooleanAttribute<Place>() && !residence.checkBooleanAttribute<Build>()
        ) {
            event.isCancelled = true
            return
        }
        val block = event.block
        val type = block.type
        if (type.isAir) return
        val player = event.player
        val ignoreBlockInfo = Config.block.ignore.getByMaterial(type)
        val putCount = if (event is BlockMultiPlaceEvent) event.replacedBlockStates.size else 1
        val limit = ignoreBlockInfo?.amount ?: -1
        if (limit >= 0) {
            val info = residence.getIgnoreBlockInfo(ignoreBlockInfo!!)
            if (info.count >= limit) {
                if (!player.hasPermission("rh.place")) {
                    val itemStack = ItemStack(block.type)
                    val name = I18n.instance.getName(player, itemStack)
                    player.sendLang("action-place-limit", name)
                    event.isCancelled = true
                } else {
                    info.increaseCount(putCount)
                    residence.save()
                }
                return
            }
            info.increaseCount(putCount)
            residence.save()
        }
    }
}
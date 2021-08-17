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

package site.liangbai.realhomehunt.internal.listener.player

import org.bukkit.Material
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import site.liangbai.realhomehunt.api.cache.SelectCache
import site.liangbai.realhomehunt.api.locale.manager.LocaleManager
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager
import site.liangbai.realhomehunt.common.config.Config
import taboolib.common.platform.event.SubscribeEvent

internal object ListenerPlayerInteract {
    @SubscribeEvent(ignoreCancelled = true)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player
        if (!ResidenceManager.isOpened(event.player.world)) return
        val itemStack = event.item
        var itemType: Material? = null
        if (itemStack == null || itemStack.type.also { itemType = it } == Material.AIR) return
        if (!isLeftSelectTool(itemType!!) && !isRightSelectTool(itemType!!)) return
        val block = event.clickedBlock
        if (block == null || block.type == Material.AIR) return
        val locale = LocaleManager.require(player)
        if (!player.hasPermission("rh.select")) {
            player.sendMessage(locale.asString("action.select.haveNotPermission", "rh.select"))
            return
        }
        if (!player.hasPermission("rh.unlimited.select") && ResidenceManager.getResidenceByOwner(player.name) != null) {
            player.sendMessage(locale.asString("action.select.alreadyHaveResidence"))
            return
        }
        val action = event.action
        if (action == Action.LEFT_CLICK_BLOCK) {
            if (!isLeftSelectTool(itemType!!)) return
            SelectCache.push(SelectCache.SelectType.FIRST, player, block.location)
            player.sendMessage(locale.asString("action.select.selectFirst"))
            event.isCancelled = true
        }
        if (action == Action.RIGHT_CLICK_BLOCK) {
            if (!isRightSelectTool(itemType!!)) return
            SelectCache.push(SelectCache.SelectType.SECOND, player, block.location)
            player.sendMessage(locale.asString("action.select.selectSecond"))
            event.isCancelled = true
        }
    }

    private fun isLeftSelectTool(material: Material): Boolean {
        return Config.residence.tool.leftSelect == material
    }

    private fun isRightSelectTool(material: Material): Boolean {
        return Config.residence.tool.rightSelect == material
    }
}
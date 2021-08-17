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

import org.bukkit.block.Container
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import site.liangbai.realhomehunt.api.residence.attribute.impl.OpenDoorAttribute
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager
import site.liangbai.realhomehunt.common.config.Config
import site.liangbai.realhomehunt.util.Blocks
import taboolib.common.platform.event.SubscribeEvent

internal object ListenerPlayerClick {
    @SubscribeEvent
    fun onPlayerInteractDoor(event: PlayerInteractEvent) {
        if (!checkInteract(event)) return
        val block = event.clickedBlock ?: return
        if (!Blocks.isDoor(block)) return
        val residence = ResidenceManager.getResidenceByLocation(block.location)
            ?: return
        if (!residence.isAdministrator(event.player) && !residence.checkBooleanAttribute(OpenDoorAttribute::class.java)) event.isCancelled =
            true
    }

    @SubscribeEvent
    fun onPlayerInteractContainer(event: PlayerInteractEvent) {
        if (!checkInteract(event)) return
        val block = event.clickedBlock ?: return
        if (block.state !is Container) return
        val residence = ResidenceManager.getResidenceByLocation(block.location)
            ?: return
        if (Config.robChestMode.enabled && !residence.isAdministrator(event.player)) event.isCancelled = true
    }

    private fun checkInteract(event: PlayerInteractEvent): Boolean {
        if (event.player.hasPermission("rh.interact")) return false
        return if (!ResidenceManager.isOpened(event.player.world)) false else event.action == Action.RIGHT_CLICK_BLOCK
    }
}
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

package site.liangbai.realhomehunt.listener.player;

import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import site.liangbai.dynamic.event.EventSubscriber;
import site.liangbai.realhomehunt.api.residence.Residence;
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager;
import site.liangbai.realhomehunt.config.Config;
import site.liangbai.realhomehunt.util.Blocks;

@EventSubscriber
public class ListenerPlayerClick implements Listener {
    @EventHandler
    public void onPlayerInteractDoor(PlayerInteractEvent event) {
        if (!checkInteract(event)) return;

        Block block = event.getClickedBlock();

        if (block == null) return;

        if (!Blocks.isDoor(block)) return;

        Residence residence = ResidenceManager.getResidenceByLocation(block.getLocation());

        if (residence == null) return;

        if (!residence.isAdministrator(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractContainer(PlayerInteractEvent event) {
        if (!checkInteract(event)) return;

        Block block = event.getClickedBlock();

        if (block == null) return;

        if (!(block.getState() instanceof Container)) return;

        Residence residence = ResidenceManager.getResidenceByLocation(block.getLocation());

        if (residence == null) return;

        if (Config.robChestMode.enabled && !residence.isAdministrator(event.getPlayer())) event.setCancelled(true);
    }

    private static boolean checkInteract(PlayerInteractEvent event) {
        if (event.getPlayer().hasPermission("rh.interact")) return false;

        if (!ResidenceManager.isOpened(event.getPlayer().getWorld())) return false;

        return event.getAction() == Action.RIGHT_CLICK_BLOCK;
    }
}

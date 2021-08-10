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

package site.liangbai.realhomehunt.listener.player.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockSpreadEvent;
import site.liangbai.dynamic.event.EventSubscriber;
import site.liangbai.realhomehunt.api.residence.Residence;
import site.liangbai.realhomehunt.api.residence.attribute.impl.SpreadFireAttribute;
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager;

@EventSubscriber
public final class ListenerBlockSpread implements Listener {
    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        Block block = event.getBlock();

        if (block.getType() != Material.FIRE) return;

        if (!ResidenceManager.isOpened(block.getWorld())) return;

        Residence residence = ResidenceManager.getResidenceByLocation(block.getLocation());

        if (residence != null) {
            if (!residence.checkBooleanAttribute(SpreadFireAttribute.class)) event.setCancelled(true);
        }
    }
}

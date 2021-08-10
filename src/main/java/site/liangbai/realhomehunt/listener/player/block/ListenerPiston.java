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

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import site.liangbai.dynamic.event.EventSubscriber;
import site.liangbai.realhomehunt.api.residence.Residence;
import site.liangbai.realhomehunt.api.residence.attribute.impl.PistonAttribute;
import site.liangbai.realhomehunt.api.residence.attribute.impl.PistonProtectionAttribute;
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager;

/**
 * The type Listener piston.
 *
 * @author Liangbai
 * @since 2021 /08/10 01:24 下午
 */
@EventSubscriber
public final class ListenerPiston implements Listener {
    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        if (!ResidenceManager.isOpened(event.getBlock().getWorld())) return;

        Residence residence = ResidenceManager.getResidenceByLocation(event.getBlock().getLocation());

        if (residence != null && !residence.checkBooleanAttribute(PistonAttribute.class)) {
            event.setCancelled(true);
        }

        for (Block block : event.getBlocks()) {
            Location location = block.getLocation().clone();

            Residence from = ResidenceManager.getResidenceByLocation(location);

            BlockFace direction = event.getDirection();

            Residence to = ResidenceManager.getResidenceByLocation(location.clone().add(direction.getModX(), direction.getModY(), direction.getModZ()));

            if ((from != null && from.checkBooleanAttribute(PistonProtectionAttribute.class))
                    || (to != null && to.checkBooleanAttribute(PistonProtectionAttribute.class))
            ) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        if (!ResidenceManager.isOpened(event.getBlock().getWorld())) return;

        Residence residence = ResidenceManager.getResidenceByLocation(event.getBlock().getLocation());

        if (residence != null && !residence.checkBooleanAttribute(PistonAttribute.class)) {
            event.setCancelled(true);
        }

        if (event.isSticky()) {
            for (Block block : event.getBlocks()) {
                residence = ResidenceManager.getResidenceByLocation(block.getLocation().clone());

                if (residence != null && residence.checkBooleanAttribute(PistonProtectionAttribute.class)) {
                    event.setCancelled(true);

                    return;
                }
            }
        }
    }
}

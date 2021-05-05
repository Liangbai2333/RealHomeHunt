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

package site.liangbai.realhomehunt.listener.entity;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import site.liangbai.lrainylib.annotation.Plugin;
import site.liangbai.realhomehunt.manager.ResidenceManager;
import site.liangbai.realhomehunt.residence.Residence;
import site.liangbai.realhomehunt.residence.attribute.impl.ExplodeAttribute;

import java.util.List;
import java.util.stream.Collectors;

@Plugin.EventSubscriber
public final class ListenerEntityExplode implements Listener {
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (!ResidenceManager.isOpened(event.getEntity().getWorld())) return;

        List<Block> blocks = event.blockList();

        blocks.stream()
                .filter(it -> {
                    if (it == null) return false;

                    if (it.getType().isAir()) return false;

                    Residence residence = ResidenceManager.getResidenceByLocation(it.getLocation());

                    try {
                        return residence != null && !residence.getAttribute(ExplodeAttribute.class).get();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();

                        return false;
                    }
                })
                .collect(Collectors.toSet())
                .forEach(blocks::remove);
    }
}

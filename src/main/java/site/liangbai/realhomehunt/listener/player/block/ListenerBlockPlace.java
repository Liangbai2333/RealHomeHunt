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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import site.liangbai.dynamic.event.EventSubscriber;
import site.liangbai.realhomehunt.api.locale.impl.Locale;
import site.liangbai.realhomehunt.api.locale.manager.LocaleManager;
import site.liangbai.realhomehunt.api.residence.Residence;
import site.liangbai.realhomehunt.api.residence.attribute.impl.BuildAttribute;
import site.liangbai.realhomehunt.api.residence.attribute.impl.PlaceAttribute;
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager;
import site.liangbai.realhomehunt.config.Config;

@EventSubscriber
public final class ListenerBlockPlace implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!ResidenceManager.isOpened(event.getPlayer().getWorld())) return;

        Residence residence = ResidenceManager.getResidenceByLocation(event.getBlock().getLocation());

        if (residence == null) return;

        if (!residence.isAdministrator(event.getPlayer()) && !event.getPlayer().hasPermission("rh.place")
                && !residence.checkBooleanAttribute(PlaceAttribute.class) && !residence.checkBooleanAttribute(BuildAttribute.class)) {
            event.setCancelled(true);

            return;
        }

        Block block = event.getBlock();

        Material type = block.getType();

        if (type.isAir()) return;

        Player player = event.getPlayer();

        Config.BlockSetting.BlockIgnoreSetting.IgnoreBlockInfo ignoreBlockInfo = Config.block.ignore.getByMaterial(type);

        int limit = ignoreBlockInfo != null ? ignoreBlockInfo.amount : -1;

        if (limit >= 0) {
            Residence.IgnoreBlockInfo info = residence.getIgnoreBlockInfo(ignoreBlockInfo);

            if (info.getCount() >= limit) {
                if (!player.hasPermission("rh.place")) {
                    Locale locale = LocaleManager.require(player);

                    player.sendMessage(locale.asString("action.place.limit", type.name().toLowerCase()));

                    event.setCancelled(true);
                } else {
                    info.increaseCount();

                    residence.save();
                }

                return;
            }

            info.increaseCount();

            residence.save();
        }
    }
}

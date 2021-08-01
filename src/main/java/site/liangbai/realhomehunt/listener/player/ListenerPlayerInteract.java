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

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import site.liangbai.lrainylib.annotation.Plugin;
import site.liangbai.realhomehunt.api.cache.SelectCache;
import site.liangbai.realhomehunt.config.Config;
import site.liangbai.realhomehunt.api.locale.impl.Locale;
import site.liangbai.realhomehunt.api.locale.manager.LocaleManager;
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager;

@Plugin.EventSubscriber
public final class ListenerPlayerInteract implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!ResidenceManager.isOpened(event.getPlayer().getWorld())) return;

        ItemStack itemStack = event.getItem();

        Material itemType;

        if (itemStack == null || (itemType = itemStack.getType()) == Material.AIR) return;

        if (!isLeftSelectTool(itemType) && !isRightSelectTool(itemType)) return;

        Block block = event.getClickedBlock();

        if (block == null || block.getType() == Material.AIR) return;

        Locale locale = LocaleManager.require(player);

        if (!player.hasPermission("rh.select")) {
            player.sendMessage(locale.asString("action.select.haveNotPermission", "rh.select"));

            return;
        }

        if (ResidenceManager.getResidenceByOwner(player.getName()) != null) {
            player.sendMessage(locale.asString("action.select.alreadyHaveResidence"));

            return;
        }

        Action action = event.getAction();

        if (action == Action.LEFT_CLICK_BLOCK) {
            if (!isLeftSelectTool(itemType)) return;

            SelectCache.push(SelectCache.SelectType.FIRST, player.getName(), block.getLocation());

            player.sendMessage(locale.asString("action.select.selectFirst"));

            event.setCancelled(true);
        }

        if (action == Action.RIGHT_CLICK_BLOCK) {
            if (!isRightSelectTool(itemType)) return;

            SelectCache.push(SelectCache.SelectType.SECOND, player.getName(), block.getLocation());

            player.sendMessage(locale.asString("action.select.selectSecond"));

            event.setCancelled(true);
        }
    }

    private boolean isLeftSelectTool(Material material) {
        return Config.residence.tool.leftSelect == material;
    }

    private boolean isRightSelectTool(Material material) {
        return Config.residence.tool.rightSelect == material;
    }
}

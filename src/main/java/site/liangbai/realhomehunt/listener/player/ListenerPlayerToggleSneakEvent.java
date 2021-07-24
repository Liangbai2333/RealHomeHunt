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

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import site.liangbai.lrainylib.annotation.Plugin;
import site.liangbai.realhomehunt.config.Config;
import site.liangbai.realhomehunt.locale.impl.Locale;
import site.liangbai.realhomehunt.locale.manager.LocaleManager;
import site.liangbai.realhomehunt.manager.ResidenceManager;
import site.liangbai.realhomehunt.residence.Residence;

@Plugin.EventSubscriber
public final class ListenerPlayerToggleSneakEvent implements Listener {
    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        if (!Config.residence.openWarn) return;

        Player player = event.getPlayer();

        Location location = player.getLocation();

        if (location.getPitch() > -85 || !event.isSneaking()) {
            return;
        }

        Residence residence = ResidenceManager.getResidenceByLocation(location);

        Locale locale = LocaleManager.require(player);

        if (residence == null) {
            player.sendMessage(locale.asString("action.warn.haveNotResidence"));

            return;
        }

        if (!residence.isAdministrator(player)) {
            player.sendMessage(locale.asString("action.warn.isNotAdministrator"));

            return;
        }

        if (!residence.canWarn()) {
            player.sendMessage(locale.asString("action.warn.waitMessage"));

            return;
        }

        residence.warn(player);
    }
}

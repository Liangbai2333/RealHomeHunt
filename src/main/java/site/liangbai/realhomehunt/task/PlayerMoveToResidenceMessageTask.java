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

package site.liangbai.realhomehunt.task;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import site.liangbai.realhomehunt.api.locale.impl.Locale;
import site.liangbai.realhomehunt.api.locale.manager.LocaleManager;
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager;
import site.liangbai.realhomehunt.api.residence.Residence;
import site.liangbai.realhomehunt.util.Locations;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerMoveToResidenceMessageTask extends BukkitRunnable {
    private final Map<String, String> moveToResidenceCache = new ConcurrentHashMap<>();

    public static void setup(Plugin plugin) {
        new PlayerMoveToResidenceMessageTask().runTaskTimerAsynchronously(plugin, 1, 1);
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().stream()
                .filter(it -> !it.isDead() && ResidenceManager.isOpened(it.getWorld()))
                .forEach(it -> {
                    Locale locale = LocaleManager.require(it);

                    String name = it.getName();

                    Location location = Locations.toBlockLocation(it.getLocation());

                    Residence residence = ResidenceManager.getResidenceByLocation(location);

                    if (residence == null) {
                        if (moveToResidenceCache.containsKey(name)) {
                            String other = moveToResidenceCache.get(name);

                            it.sendMessage(locale.asString("action.residence.moveOut", other));

                            moveToResidenceCache.remove(name);
                        }
                    } else {
                        String lastResidence = moveToResidenceCache.get(name);

                        if (!residence.getOwner().equals(lastResidence)) {
                            String other = residence.getOwner();

                            if (lastResidence != null) {
                                it.sendMessage(locale.asString("action.residence.moveOut", lastResidence));
                            }

                            it.sendMessage(locale.asString("action.residence.moveIn", other));

                            moveToResidenceCache.put(name, other);
                        }
                    }
                });
    }
}

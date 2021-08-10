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
import site.liangbai.realhomehunt.api.residence.Residence;
import site.liangbai.realhomehunt.api.residence.attribute.impl.GlowAttribute;
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager;
import site.liangbai.realhomehunt.util.Locations;

/**
 * The type Player glow task.
 *
 * @author Liangbai
 * @since 2021 /08/10 11:07 上午
 */
public final class PlayerGlowTask extends BukkitRunnable  {
    public static void setup(Plugin plugin) {
        new PlayerGlowTask().runTaskTimerAsynchronously(plugin, 1, 1);
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().stream()
                .filter(it -> !it.isDead() && ResidenceManager.isOpened(it.getWorld()))
                .forEach(it -> {
                    Location location = Locations.toBlockLocation(it.getLocation());

                    Residence residence = ResidenceManager.getResidenceByLocation(location);

                    if (residence != null) {
                        if (residence.checkBooleanAttribute(GlowAttribute.class) && !it.isGlowing()) {
                            it.setGlowing(true);
                        }
                    } else if (it.isGlowing()) {
                        it.setGlowing(false);
                    }
                });
    }
}

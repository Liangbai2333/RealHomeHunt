/*
 * RealHomeHunt
 * Copyright (C) 2022  Liangbai
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

package site.liangbai.realhomehunt.util;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * The type Particles.
 *
 * @author Liangbai
 * @since 2021 /08/12 11:46 上午
 */
public class Particles {
    public static <T> void spawnParticle(Particle particle, List<Player> receivers, Location location, T data) {
        spawnParticle(particle, receivers, null, location.getX(), location.getY(), location.getZ(), 0, 0, 0, 0, 1, data, true);
    }

    public static  <T> void spawnParticle(Particle particle, List<Player> receivers, Player sender, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, T data, boolean force) {
        receivers.forEach(player -> player.spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, extra, data));
    }
}

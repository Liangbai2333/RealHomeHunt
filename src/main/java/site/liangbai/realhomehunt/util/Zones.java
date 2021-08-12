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

package site.liangbai.realhomehunt.util;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import site.liangbai.realhomehunt.common.config.Config;
import site.liangbai.realhomehunt.common.particle.EffectGroup;
import site.liangbai.realhomehunt.common.particle.Line;

import java.util.Objects;

/**
 * The type Zones.
 * 初中生, 水平受限
 *
 * @author Liangbai
 * @since 2021 /08/12 10:15 上午
 */
public final class Zones {
    public static EffectGroup startShowWithBlockLocation(Player player, Location left, Location right) {
        BoundingBox box = BoundingBox.of(left.clone(), right.clone());

        World world = left.getWorld();

        Location min = box.getMin().toLocation(world).clone();
        Location max = box.getMax().toLocation(world).clone();

        return Zones.getZoneEffectGroup(min.add(-0.2, -0.2, -0.2), max.add(1.2, 1.2, 1.2),
                        Config.residence.tool.showParticleStep)
                .setParticle(Config.residence.tool.showParticle)
                .setColor(Config.residence.tool.particleColor.enabled ? Config.residence.tool.particleColor.color : (Config.residence.tool.showParticle == Particle.REDSTONE ? Color.RED : null))
                .setPeriod(2)
                .alwaysShowAsync(player);
    }

    public static EffectGroup getZoneEffectGroup(Location left, Location right) {
        return getZoneEffectGroup(left, right, 0.1);
    }

    public static EffectGroup getZoneEffectGroup(Location left, Location right, double step) {
        return getZoneEffectGroup(left, right, step, null);
    }

    public static EffectGroup getZoneEffectGroup(Location left, Location right, double step, Color color) {
        if (!Objects.equals(left.getWorld(), right.getWorld())) {
            throw new IllegalArgumentException("the two points' world must be same.");
        }

        World world = left.getWorld();

        BoundingBox box = BoundingBox.of(left, right);

        double minX = box.getMinX();
        double minY = box.getMinY();
        double minZ = box.getMinZ();

        double maxX = box.getMaxX();
        double maxY = box.getMaxY();
        double maxZ = box.getMaxZ();

        Location pos1 = new Location(world, minX, minY, minZ);
        Location pos2 = new Location(world, minX, minY, maxZ);
        Location pos3 = new Location(world, minX, maxY, minZ);
        Location pos4 = new Location(world, maxX, minY, minZ);
        Location pos5 = new Location(world, maxX, maxY, minZ);
        Location pos6 = new Location(world, maxX, maxY, maxZ);
        Location pos7 = new Location(world, maxX, minY, maxZ);
        Location pos8 = new Location(world, minX, maxY, maxZ);

        Line line1 = new Line(pos1, pos2).setStep(step);
        Line line2 = line1.clone().setStart(pos1).setEnd(pos3);
        Line line3 = line1.clone().setStart(pos1).setEnd(pos4);
        Line line4 = line1.clone().setStart(pos6).setEnd(pos5);
        Line line5 = line1.clone().setStart(pos6).setEnd(pos7);
        Line line6 = line1.clone().setStart(pos6).setEnd(pos8);
        Line line7 = line1.clone().setStart(pos3).setEnd(pos5);
        Line line8 = line1.clone().setStart(pos3).setEnd(pos8);
        Line line9 = line1.clone().setStart(pos7).setEnd(pos4);
        Line line10 = line1.clone().setStart(pos7).setEnd(pos2);
        Line line11 = line1.clone().setStart(pos4).setEnd(pos5);
        Line line12 = line1.clone().setStart(pos2).setEnd(pos8);

        return new EffectGroup()
                .addEffect(line1)
                .addEffect(line2)
                .addEffect(line3)
                .addEffect(line4)
                .addEffect(line5)
                .addEffect(line6)
                .addEffect(line7)
                .addEffect(line8)
                .addEffect(line9)
                .addEffect(line10)
                .addEffect(line11)
                .addEffect(line12)
                .setColor(color);
    }
}

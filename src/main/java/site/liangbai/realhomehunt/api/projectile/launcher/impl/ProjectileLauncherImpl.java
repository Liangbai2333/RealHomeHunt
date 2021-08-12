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

package site.liangbai.realhomehunt.api.projectile.launcher.impl;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import site.liangbai.realhomehunt.RealHomeHuntPlugin;
import site.liangbai.realhomehunt.api.projectile.AbstractProjectile;
import site.liangbai.realhomehunt.api.projectile.launcher.IProjectileLauncher;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class ProjectileLauncherImpl implements IProjectileLauncher {
    @Override
    public void launch(AbstractProjectile projectile, Vector velocity, Consumer<RayTraceResult> callback) {
        Entity entity = projectile.getProjectileEntity();

        Entity holder = projectile.getProjectileHolder();

        entity.setVelocity(velocity);

        new BukkitRunnable() {

            private final List<Object> impactedList = new ArrayList<>();

            @Override
            public void run() {
                Location location = entity.getLocation().clone();

                Optional<Entity> hitEntity = location.getWorld().getNearbyEntities(location, 1, 1, 1).stream()
                        .filter(it -> !Objects.equals(entity, it))
                        .filter(it -> !Objects.equals(holder, it))
                        .filter(it -> !(it instanceof Item))
                        .filter(it -> !impactedList.contains(it))
                        .findAny();

                // while if none match hit's entity
                boolean hitBlock = entity.isDead();

                if (hitBlock || hitEntity.isPresent()) {

                    RayTraceResult result;

                    if (hitEntity.isPresent()) {
                        Entity hitEntityObj = hitEntity.orElse(null);

                        impactedList.add(hitEntityObj);

                        result = new RayTraceResult(location.toVector(), hitEntityObj, entity.getFacing().getOppositeFace());
                    } else {
                        Block block = location.getBlock();

                        // May produce an empty result.
                        result = new RayTraceResult(location.toVector(), block, entity.getFacing().getOppositeFace());
                    }

                    callback.accept(result);

                    if (hitBlock) {
                        entity.remove();

                        cancel();
                    }
                }
            }
        }.runTaskTimer(RealHomeHuntPlugin.getInst(), 0, 1);
    }
}

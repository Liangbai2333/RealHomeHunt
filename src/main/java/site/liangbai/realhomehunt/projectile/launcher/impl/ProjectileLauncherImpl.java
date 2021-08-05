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

package site.liangbai.realhomehunt.projectile.launcher.impl;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import site.liangbai.realhomehunt.RealHomeHunt;
import site.liangbai.realhomehunt.projectile.AbstractProjectile;
import site.liangbai.realhomehunt.projectile.launcher.IProjectileLauncher;

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

            @Override
            public void run() {
                Location location = entity.getLocation();

                Optional<Entity> hitEntity = location.getWorld().getNearbyEntities(location, 1, 1, 1).stream()
                        .filter(it -> !Objects.equals(entity, it))
                        .filter(it -> !Objects.equals(holder, it))
                        .filter(it -> !(it instanceof Item))
                        .findAny();

                // while if none match hit's entity
                boolean hitBlock = entity instanceof FallingBlock && entity.isDead();

                boolean faceHitBlock = false;

                Block faceBlock = entity.getLocation().getBlock().getRelative(entity.getFacing());

                if (!hitBlock && hitEntity.isEmpty() && !(entity instanceof FallingBlock)) {
                    faceHitBlock = !faceBlock.getType().isAir();
                }

                if (hitBlock || hitEntity.isPresent() || faceHitBlock) {
                    Location hitPosition = location.clone();

                    RayTraceResult result;

                    if (hitEntity.isPresent()) {
                        result = new RayTraceResult(hitPosition.toVector(), hitEntity.orElse(null), entity.getFacing().getOppositeFace());
                    } else if (hitBlock){
                        Block block = hitPosition.getBlock();

                        // May produce an empty result.
                        result = new RayTraceResult(hitPosition.toVector(), block, entity.getFacing().getOppositeFace());
                    } else {
                        result = new RayTraceResult(faceBlock.getLocation().toVector(), faceBlock, entity.getFacing().getOppositeFace());
                    }

                    entity.remove();

                    projectile.onImpact(result);

                    cancel();
                }
            }
        }.runTaskTimer(RealHomeHunt.getInst(), 0, 1);
    }
}

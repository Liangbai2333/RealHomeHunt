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

package site.liangbai.realhomehunt.api.projectile;

import org.apache.commons.lang3.Validate;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.util.Consumer;
import org.bukkit.util.RayTraceResult;

public class BlockProjectile extends AbstractProjectile {
    private final FallingBlock projectileEntity;
    private final Entity projectileHolder;

    private Consumer<RayTraceResult> callback = it -> { };

    public BlockProjectile(BlockData blockData, Entity projectileHolder, Location spawnLocation) {
        Validate.isTrue(blockData.getMaterial().isBlock(), "block projectile material must be a block");

        World world = spawnLocation.getWorld();

        Validate.notNull(world, "spawn world can not be null");

        this.projectileEntity = spawnLocation.getWorld().spawnFallingBlock(spawnLocation, blockData);
        this.projectileHolder = projectileHolder;
    }

    public BlockProjectile impact(Consumer<RayTraceResult> callback) {
        this.callback = callback;

        return this;
    }

    @Override
    public FallingBlock getProjectileEntity() {
        return projectileEntity;
    }

    @Override
    public Entity getProjectileHolder() {
        return projectileHolder;
    }

    @Override
    public void onImpact(RayTraceResult rayTraceResult) {
        callback.accept(rayTraceResult);
    }
}

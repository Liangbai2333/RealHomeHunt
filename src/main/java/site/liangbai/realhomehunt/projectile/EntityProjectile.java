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

package site.liangbai.realhomehunt.projectile;

import org.bukkit.entity.Entity;
import org.bukkit.util.Consumer;
import org.bukkit.util.RayTraceResult;

public class EntityProjectile extends AbstractProjectile {
    private final Entity projectileEntity;
    private final Entity projectileHolder;

    private Consumer<RayTraceResult> callback = it -> { };

    public EntityProjectile(Entity projectileEntity, Entity projectileHolder) {
        this.projectileEntity = projectileEntity;
        this.projectileHolder = projectileHolder;
    }

    public EntityProjectile impact(Consumer<RayTraceResult> callback) {
        this.callback = callback;

        return this;
    }

    @Override
    public Entity getProjectileEntity() {
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

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

package site.liangbai.realhomehunt.api.projectile;

import org.bukkit.entity.Entity;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import site.liangbai.realhomehunt.api.projectile.launcher.IProjectileLauncher;
import site.liangbai.realhomehunt.api.projectile.launcher.impl.ProjectileLauncherImpl;

public abstract class AbstractProjectile {
    public static final IProjectileLauncher PROJECTILE_LAUNCHER_IMPL = new ProjectileLauncherImpl();

    public abstract Entity getProjectileEntity();

    public abstract Entity getProjectileHolder();

    public abstract void onImpact(RayTraceResult rayTraceResult);

    public void launch(Vector velocity, double multiply) {
        launch(velocity.multiply(multiply));
    };

    public void launch(Vector velocity) {
        PROJECTILE_LAUNCHER_IMPL.launch(this, velocity, this::onImpact);
    }
}

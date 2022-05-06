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

package site.liangbai.realhomehunt.api.projectile.util

import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Entity
import org.bukkit.util.RayTraceResult
import org.bukkit.util.Vector
import site.liangbai.realhomehunt.api.projectile.BlockProjectile

fun BlockData.launchProjectile(holder: Entity?,
                               spawnLocation: Location,
                               velocity: Vector = spawnLocation.direction,
                               multiple: Double = 1.0,
                               onImpact: (RayTraceResult) -> Unit)
{
    BlockProjectile(this, holder, spawnLocation).impact(onImpact).launch(velocity, multiple)
}

fun Block.launchProjectile(holder: Entity?,
                           spawnLocation: Location,
                           velocity: Vector = spawnLocation.direction,
                           multiple: Double = 1.0,
                           onImpact: (RayTraceResult) -> Unit)
{
    blockData.launchProjectile(holder, spawnLocation, velocity, multiple, onImpact)
}
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

@file:JvmName("Utils")
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

package site.liangbai.realhomehunt.util.kt

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.World
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.util.BoundingBox
import org.bukkit.util.Vector
import site.liangbai.realhomehunt.api.nms.NMS
import site.liangbai.realhomehunt.common.config.Config
import java.util.regex.Pattern

private val BOOLEAN_PATTERN = Pattern.compile("true|false")

fun CharSequence.isBoolean() = BOOLEAN_PATTERN.matcher(this).matches()

fun ItemStack.toBukkitItemStack() = NMS.INSTANCE.toBukkitItemStack(this)

fun Any.toBukkitWorld() = NMS.INSTANCE.toBukkitWorld(this)

fun BlockPos.toLocation() = NMS.INSTANCE.toBukkitLocation(this)

fun Entity.toBukkitEntity() = NMS.INSTANCE.toBukkitEntity(this)

fun org.bukkit.inventory.ItemStack.toMinecraftItemStack() = NMS.INSTANCE.toMinecraftItemStack(this)

fun String.colored() = ChatColor.translateAlternateColorCodes('&', this)

fun Long.asTicks() = this * 20

fun ItemStack.asItem() = NMS.INSTANCE.asMinecraftItem(this)

fun boundingBoxOf(left: Location, right: Location, isBlockPos: Boolean = false, clone: Boolean = true): BoundingBox {
    val left0 = if (clone) left.clone() else left
    val right0 = if  (clone) right.clone() else right

    return BoundingBox.of(left0, right0).also { it.overlaps(it.min, it.max.apply { if (isBlockPos) add(Vector(1.0, 1.0, 1.0)) }) }
}

operator fun Vector.minus(other: Vector): Vector {
    return this.subtract(other)
}

operator fun Vector.plus(other: Vector): Vector {
    return this.add(other)
}

fun Material.isIgnoreGun() = Config.gun.ignore.isIgnored(this)

fun Material.isIgnoreHitBlock() = Config.block.ignore.isIgnoreHit(this)
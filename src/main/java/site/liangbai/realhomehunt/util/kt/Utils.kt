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

import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.bukkit.ChatColor
import site.liangbai.realhomehunt.api.nms.NMS
import java.util.regex.Pattern

private val BOOLEAN_PATTERN = Pattern.compile("true|false")

fun CharSequence.isBoolean() = BOOLEAN_PATTERN.matcher(this).matches()

fun ItemStack.toBukkitItemStack() = NMS.INSTANCE.toBukkitItemStack(this)

fun World.toBukkitWorld() = NMS.INSTANCE.toBukkitWorld(this)

fun BlockPos.toLocation() = NMS.INSTANCE.toBukkitLocation(this)

fun Entity.toBukkitEntity() = NMS.INSTANCE.toBukkitEntity(this)

fun String.colored() = ChatColor.translateAlternateColorCodes('&', this)

fun Long.asTicks() = this * 20
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
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack
import org.bukkit.entity.Player
import taboolib.module.nms.sendPacket
import java.util.function.Predicate
import java.util.regex.Pattern

private val BOOLEAN_PATTERN = Pattern.compile("true|false")

fun CharSequence.isBoolean() = BOOLEAN_PATTERN.matcher(this).matches()

fun ItemStack.toBukkitItemStack(): org.bukkit.inventory.ItemStack? = CraftItemStack.asBukkitCopy(this as net.minecraft.server.v1_16_R3.ItemStack)

fun World.toBukkitWorld(): org.bukkit.World = (this as net.minecraft.server.v1_16_R3.World).world

fun BlockPos.toLocation(): Location = Location(null, x.toDouble(), y.toDouble(), z.toDouble())

fun Entity.toBukkitEntity(): org.bukkit.entity.Entity = (this as net.minecraft.server.v1_16_R3.Entity).bukkitEntity

fun String.colored() = ChatColor.translateAlternateColorCodes('&', this)

fun Long.asTicks() = this * 20
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

package site.liangbai.realhomehunt.api.nms

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Player
import taboolib.module.nms.nmsProxy

abstract class NMS {
    abstract fun sendActionBar(player: Player, message: String)

    abstract fun sendBreakAnimationPacket(id: Int, block: Block, breakSit: Int, receivers: List<Player>)

    abstract fun sendBreakBlock(block: Block, dropItem: Boolean): Boolean

    abstract fun toBukkitEntity(entity: Entity): org.bukkit.entity.Entity

    abstract fun toBukkitLocation(blockPos: BlockPos): Location

    abstract fun toBukkitWorld(world: net.minecraft.world.level.World): org.bukkit.World

    abstract fun toBukkitWorldByName(name: String): org.bukkit.World

    abstract fun toBukkitItemStack(itemStack: ItemStack): org.bukkit.inventory.ItemStack

    abstract fun toMinecraftItemStack(itemStack: org.bukkit.inventory.ItemStack): ItemStack

    abstract fun asMinecraftItem(itemStack: ItemStack): Item

    companion object {
        @JvmStatic
        val INSTANCE by lazy {
            nmsProxy<NMS>()
        }
    }
}
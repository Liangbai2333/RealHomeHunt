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

package site.liangbai.realhomehunt.api.nms

import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Player

abstract class NMS {
    abstract fun sendActionBar(player: Player, message: String)

    abstract fun sendBreakAnimationPacket(id: Int, block: Block, breakSit: Int, receivers: List<Player>)

    abstract fun sendBreakBlock(block: Block, dropItem: Boolean)

    abstract fun toBukkitEntity(entity: Entity): org.bukkit.entity.Entity

    abstract fun toBukkitLocation(blockPos: BlockPos): Location

    abstract fun toBukkitWorld(world: World): org.bukkit.World

    abstract fun toBukkitItemStack(itemStack: ItemStack): org.bukkit.inventory.ItemStack

    companion object {
        @JvmStatic
        val INSTANCE by lazy {
            NMSImpl()
        }
    }
}
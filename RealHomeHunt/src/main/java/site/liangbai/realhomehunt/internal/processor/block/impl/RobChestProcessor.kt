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

package site.liangbai.realhomehunt.internal.processor.block.impl

import org.bukkit.block.Block
import org.bukkit.block.BlockState
import org.bukkit.block.Container
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import site.liangbai.realhomehunt.api.residence.Residence
import site.liangbai.realhomehunt.common.config.Config
import site.liangbai.realhomehunt.internal.processor.block.IBlockBreakProcessor
import site.liangbai.realhomehunt.internal.task.NaturallyDropItemTask
import site.liangbai.realhomehunt.util.Blocks
import site.liangbai.realhomehunt.util.Chances
import site.liangbai.realhomehunt.util.InventoryHelper
import site.liangbai.realhomehunt.util.callback.ICallback

/**
 * The type Rob chest game mode.
 *
 * @author Liangbai
 * @since 2021 /08/10 11:15 上午
 */
class RobChestProcessor : IBlockBreakProcessor {
    override fun isEnabled(): Boolean {
        return Config.robChestMode.enabled
    }

    override fun process(
        dropBlockItem: ICallback<Boolean>,
        residence: Residence,
        player: Player,
        gun: ItemStack,
        block: Block,
        snapshotState: BlockState,
        blockData: BlockData
    ) {
        if (snapshotState !is Container) return
        val inventory = snapshotState.inventory
        val clone = InventoryHelper.clone(inventory)
        val snapshotInventory = clone ?: snapshotState.snapshotInventory
        val dropItems: MutableList<ItemStack> = mutableListOf()
        snapshotInventory.forEach {
            if (it == null || it.type.isAir) return@forEach
            val chance = Config.robChestMode.dropItem.getChance(it)
            if (Chances.hasChance(chance)) {
                dropItems.add(it)
            }
        }
        dropItems.forEach {
            snapshotInventory.remove(it)
        }
        Blocks.CACHED_INVENTORY[snapshotState] = snapshotInventory
        inventory.clear()
        val dropLocation = block.location
        NaturallyDropItemTask.setup(dropItems, dropLocation)
        dropBlockItem.set(false)
    }
}
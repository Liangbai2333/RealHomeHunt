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

package site.liangbai.realhomehunt.internal.processor.block

import org.bukkit.block.Block
import org.bukkit.block.BlockState
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import site.liangbai.realhomehunt.api.residence.Residence
import site.liangbai.realhomehunt.util.callback.ICallback

interface IBlockBreakProcessor {
    fun isEnabled(): Boolean

    fun process(
        dropBlockItem: ICallback<Boolean>,
        residence: Residence, player: Player,
        gun: ItemStack,
        block: Block,
        snapshotState: BlockState,
        blockData: BlockData
    )
}
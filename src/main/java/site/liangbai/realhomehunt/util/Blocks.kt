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

package site.liangbai.realhomehunt.util

import com.mojang.authlib.GameProfile
import org.bukkit.Bukkit
import org.bukkit.Nameable
import org.bukkit.block.*
import org.bukkit.block.data.MultipleFacing
import org.bukkit.block.data.type.Door
import org.bukkit.inventory.Inventory
import org.bukkit.loot.Lootable
import site.liangbai.realhomehunt.api.nms.NMS
import site.liangbai.realhomehunt.api.residence.Residence
import site.liangbai.realhomehunt.common.config.Config
import site.liangbai.realhomehunt.common.config.Config.BlockSetting.BlockIgnoreSetting
import site.liangbai.realhomehunt.internal.task.NaturallyDropItemTask
import taboolib.common.reflect.Reflex.Companion.getProperty
import taboolib.common.reflect.Reflex.Companion.setProperty
import java.util.concurrent.ConcurrentHashMap

object Blocks {
    @JvmField
    val CACHED_INVENTORY: MutableMap<Container, Inventory> = ConcurrentHashMap()
    private var id = 0
    @JvmStatic
    fun nextId(): Int {
        if (id >= Int.MAX_VALUE) id = 0
        return ++id
    }

    @JvmStatic
    fun sendBreakBlockPacket(block: Block?, dropItem: Boolean): Boolean {
        return NMS.INSTANCE.sendBreakBlock(block!!, dropItem)
    }

    @JvmStatic
    fun sendBreakAnimationPacket(id: Int, block: Block, breakSit: Int) {
        val world = block.world
        NMS.INSTANCE.sendBreakAnimationPacket(id, block, breakSit, Bukkit.getOnlinePlayers().filter { it.world == world })
    }

    @JvmStatic
    fun sendClearBreakAnimationPacket(id: Int, block: Block) {
        sendBreakAnimationPacket(id, block, -1)
    }

    @JvmStatic
    fun isDoor(block: Block): Boolean {
        return block.blockData is Door
    }

    fun containsBlockAndReturnCount(info: BlockIgnoreSetting.IgnoreBlockInfo, residence: Residence): Long {
        return Locations.getRegionBlocks(residence.left, residence.right).stream()
            .parallel()
            .map { it.type }
            .filter { !it.isAir }
            .filter {
                it.name.equals(info.full, ignoreCase = true) ||
                        (info.prefix.isNotEmpty() || info.suffix.isNotEmpty() &&
                                it.name.startsWith(info.prefix) && it.name.endsWith(info.suffix))
            }
            .count()
    }

    @JvmStatic
    fun applyPlaceBlock(block: Block) {
        val data = block.blockData.clone()
        if (data is MultipleFacing) {
            data.allowedFaces.forEach {
                if (canConnected(block, it)) {
                    data.setFace(it, true)
                }
            }
        }
        block.blockData = data
    }

    private fun canConnected(current: Block, outside: BlockFace): Boolean {
        val outsideBlock = current.getRelative(outside)
        val outData = outsideBlock.blockData
        val currentData = current.blockData
        return outData.javaClass == currentData.javaClass && outData is MultipleFacing && !(currentData as MultipleFacing).hasFace(
            outside
        )
    }

    @JvmStatic
    fun applyBlockState(newState: BlockState, oldState: BlockState) {
        if (oldState is Container && Config.robChestMode.enabled && Config.robChestMode.fixItem) {
            val container = newState as Container
            val inventory = container.inventory
            val snapshotInventory =
                if (CACHED_INVENTORY.containsKey(oldState)) CACHED_INVENTORY[oldState] else oldState.snapshotInventory
            CACHED_INVENTORY.remove(oldState)
            val list = InventoryHelper.copyTo(inventory, snapshotInventory)
            if (list.isNotEmpty()) {
                NaturallyDropItemTask.setup(list, newState.getLocation())
            }
        } else if (oldState is Sign) {
            val sign = newState as Sign
            val lines = oldState.lines
            sign.isEditable = oldState.isEditable
            sign.color = oldState.color
            for (i in lines.indices) {
                sign.setLine(i, lines[i])
            }
        } else if (oldState is CommandBlock) {
            val commandBlock = newState as CommandBlock
            commandBlock.setCommand(oldState.command)
            commandBlock.setName(oldState.name)
        } else if (oldState is Skull) {
            val skull = newState as Skull
            val player = oldState.owningPlayer
            if (player != null) {
                skull.setOwningPlayer(player)
            }
            val profile = oldState.getProperty<GameProfile>("profile")
            if (profile != null) {
                skull.setProperty("profile", profile)
            }
        }
        if (oldState is Lockable) {
            (newState as Lockable).setLock((oldState as Lockable).lock)
        }
        if (oldState is Nameable) {
            (newState as Nameable).customName = (oldState as Nameable).customName
        }
        if (oldState is Lootable) {
            val lootable = newState as Lootable
            val snapshot = oldState as Lootable
            lootable.lootTable = snapshot.lootTable
            lootable.seed = snapshot.seed
        }
    }
}
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
import net.minecraft.server.v1_16_R3.BlockPosition
import net.minecraft.server.v1_16_R3.ChatMessageType
import net.minecraft.server.v1_16_R3.PacketPlayOutChat
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack
import org.bukkit.craftbukkit.v1_16_R3.util.CraftChatMessage
import org.bukkit.entity.Player
import taboolib.common.reflect.Reflex.Companion.getProperty
import taboolib.common.reflect.Reflex.Companion.setProperty
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.sendPacket

class NMSImpl : NMS() {
    val isUniversal = MinecraftVersion.isUniversal

    fun Player.sendPacketI(packet: Any, vararg fields: Pair<String, Any?>) {
        sendPacket(setFields(packet, *fields))
    }

    fun setFields(any: Any, vararg fields: Pair<String, Any?>): Any {
        fields.forEach { (key, value) ->
            if (value != null) {
                any.setProperty(key, value)
            }
        }
        return any
    }

    override fun sendActionBar(player: Player, message: String) {
        player.sendPacket(PacketPlayOutChat(CraftChatMessage.fromStringOrNull(message), ChatMessageType.GAME_INFO, player.uniqueId))
    }

    override fun sendBreakAnimationPacket(id: Int, block: Block, breakSit: Int, receivers: List<Player>) {
        val location = block.location

        val blockPos = BlockPosition(location.blockX, location.blockY, location.blockZ)

        val packet = net.minecraft.server.v1_16_R3.PacketPlayOutBlockBreakAnimation(id, blockPos, breakSit)

        receivers.forEach { it.sendPacket(packet) }
    }

    override fun sendBreakBlock(block: Block, dropItem: Boolean) {
        val world = block.world

        val location = block.location

        val craftWorld = world as CraftWorld

        val worldServer = craftWorld.handle

        val blockPosition = BlockPosition(location.blockX, location.blockY, location.blockZ)

        worldServer.b(blockPosition, dropItem)
    }

    override fun toBukkitEntity(entity: Entity): org.bukkit.entity.Entity = (entity as net.minecraft.server.v1_16_R3.Entity).bukkitEntity

    override fun toBukkitLocation(blockPos: BlockPos) = Location(null, blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble())

    override fun toBukkitWorld(world: World): org.bukkit.World = (world as net.minecraft.server.v1_16_R3.World).world

    override fun toBukkitItemStack(itemStack: ItemStack): org.bukkit.inventory.ItemStack = CraftItemStack.asCraftMirror(itemStack as net.minecraft.server.v1_16_R3.ItemStack)

    override fun toMinecraftItemStack(itemStack: org.bukkit.inventory.ItemStack): ItemStack {
        return (if (itemStack is CraftItemStack) {
            itemStack.getProperty<net.minecraft.server.v1_16_R3.ItemStack>("handle") ?: CraftItemStack.asNMSCopy(itemStack)
        } else CraftItemStack.asNMSCopy(itemStack)) as ItemStack
    }
}
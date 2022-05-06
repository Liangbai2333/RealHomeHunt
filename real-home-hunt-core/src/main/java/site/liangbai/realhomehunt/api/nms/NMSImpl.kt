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
import net.minecraft.core.BlockPosition
import net.minecraft.network.chat.ChatMessageType
import net.minecraft.network.protocol.game.PacketPlayOutBlockBreakAnimation
import net.minecraft.network.protocol.game.PacketPlayOutChat
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.storage.ServerLevelData
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.craftbukkit.v1_18_R2.CraftServer
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftEntity
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack
import org.bukkit.craftbukkit.v1_18_R2.util.CraftChatMessage
import org.bukkit.entity.Player
import taboolib.common.reflect.Reflex.Companion.getProperty
import taboolib.common.reflect.Reflex.Companion.invokeMethod
import taboolib.common.reflect.Reflex.Companion.setProperty
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.sendPacket

/**
 * 暂时没有好的办法让1.18的NMS和Forge共存，所以用反射。
 */
class NMSImpl : NMS() {
    val isUniversal = MinecraftVersion.isUniversal
    private val server = Bukkit.getServer() as CraftServer

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
        player.sendPacket(PacketPlayOutChat(CraftChatMessage.fromStringOrNull(message), ChatMessageType.values()[2], player.uniqueId))
    }

    override fun sendBreakAnimationPacket(id: Int, block: Block, breakSit: Int, receivers: List<Player>) {
        val location = block.location

        val blockPos = BlockPosition(location.blockX, location.blockY, location.blockZ)

        val packet = PacketPlayOutBlockBreakAnimation(id, blockPos, breakSit)

        receivers.forEach { it.sendPacket(packet) }
    }

    override fun sendBreakBlock(block: Block, dropItem: Boolean): Boolean {
        val world = block.world
        val location = block.location
        val craftWorld = world as CraftWorld
        // 没有办法，之后再看看怎么改动吧
        val worldServer = craftWorld.getProperty<Any>("handle")!!
        val blockPosition = BlockPosition(location.blockX, location.blockY, location.blockZ)
        return worldServer.invokeMethod("b", blockPosition, dropItem)!!
    }

    override fun toBukkitEntity(entity: Entity): org.bukkit.entity.Entity = CraftEntity.getEntity(server, entity)

    override fun toBukkitLocation(blockPos: BlockPos) = Location(null, blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble())

    override fun toBukkitWorld(world: Any): World = toBukkitWorldByName(world.getProperty<ServerLevelData>("serverLevelData")!!.levelName)
    override fun toBukkitWorldByName(name: String): World = server.getWorld(name)!!

    override fun toBukkitItemStack(itemStack: ItemStack): org.bukkit.inventory.ItemStack = CraftItemStack.asCraftMirror(itemStack)

    override fun toMinecraftItemStack(itemStack: org.bukkit.inventory.ItemStack): ItemStack {
        return (if (itemStack is CraftItemStack) {
            itemStack.getProperty<ItemStack>("handle") ?: CraftItemStack.asNMSCopy(itemStack)
        } else CraftItemStack.asNMSCopy(itemStack)) as ItemStack
    }

    override fun asMinecraftItem(itemStack: ItemStack): Item = itemStack.item
}
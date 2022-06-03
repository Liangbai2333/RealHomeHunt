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

package site.liangbai.realhomehunt.internal.listener.forge.arclight

import com.craftingdead.core.event.GunEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import org.bukkit.entity.Player
import site.liangbai.realhomehunt.internal.processor.Processors
import site.liangbai.realhomehunt.util.kt.toBukkitEntity
import site.liangbai.realhomehunt.util.kt.toBukkitItemStack
import site.liangbai.realhomehunt.util.kt.toBukkitWorld
import site.liangbai.realhomehunt.util.kt.toLocation
import taboolib.common.Isolated
import taboolib.common.reflect.Reflex.Companion.getProperty

@Isolated
class EventHandlerGunHitBlock {
    @SubscribeEvent
    fun onGunHitBlock(event: GunEvent.HitBlock) {
        val gunItem = event.gun
        if (!gunItem.isTriggerPressed || gunItem.ammoProvider.expectedMagazine.size == 0) return
        val player = event.living.entity.toBukkitEntity() as? Player ?: return
        val gun = event.itemStack.toBukkitItemStack()
        val world = event.getProperty<Any>("level")!!.toBukkitWorld()
        val block = world.getBlockAt(event.rayTraceResult.blockPos.toLocation())
        Processors.GUN_HIT_BLOCK_PROCESSOR.processGunHitBlock(player, gun, block)
    }
}
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

package site.liangbai.realhomehunt.internal.task

import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import site.liangbai.realhomehunt.api.residence.attribute.impl.Glow
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager
import site.liangbai.realhomehunt.util.Locations
import site.liangbai.realhomehunt.util.kt.filterNotActive
import site.liangbai.realhomehunt.util.kt.filterNotOpenedWorld
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import java.util.*
import java.util.concurrent.CopyOnWriteArraySet

/**
 * The type Player glow task.
 *
 * @author Liangbai
 * @since 2021 /08/10 11:07 上午
 */
internal object PlayerGlowTask {
    private val glowing: MutableSet<UUID> = CopyOnWriteArraySet()

    @Awake(LifeCycle.ACTIVE)
    fun setup() {
        submit(async = true, delay = 20, period = 1) {
            Bukkit.getOnlinePlayers()
                .filterNotActive()
                .filterNotOpenedWorld()
                .forEach {
                    val location = Locations.toBlockLocation(
                        it.location
                    )
                    val residence = ResidenceManager.getResidenceByLocation(location)
                    if (residence != null) {
                        if (residence.checkBooleanAttribute<Glow>() && !it.isGlowing) {
                            it.isGlowing = true
                            glowing.add(it.uniqueId)
                        } else if (!residence.checkBooleanAttribute<Glow>() && it.isGlowing) {
                            it.isGlowing = false
                            glowing.remove(it.uniqueId)
                        }
                    } else if (it.isGlowing) {
                        if (glowing.contains(it.uniqueId)) {
                            it.isGlowing = false
                            glowing.remove(it.uniqueId)
                        }
                    } else glowing.remove(it.uniqueId)
                }
        }
    }

    @SubscribeEvent
    fun onPlayerQuit(event: PlayerQuitEvent) {
        if (event.player.isGlowing && glowing.contains(event.player.uniqueId)) {
            event.player.isGlowing = false
            glowing.remove(event.player.uniqueId)
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (event.player.isGlowing) {
            event.player.isGlowing = false
        }
    }
}
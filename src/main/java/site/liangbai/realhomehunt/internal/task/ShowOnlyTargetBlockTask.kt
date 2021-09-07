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

package site.liangbai.realhomehunt.internal.task

import org.bukkit.Bukkit
import org.bukkit.block.Block
import org.bukkit.entity.Player
import site.liangbai.realhomehunt.api.cache.DamageCachePool.DamageCache
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager
import site.liangbai.realhomehunt.common.config.Config
import site.liangbai.realhomehunt.internal.processor.Processors
import site.liangbai.realhomehunt.util.Guns
import site.liangbai.realhomehunt.util.RayTraceUtil
import site.liangbai.realhomehunt.util.kt.filterNotActive
import site.liangbai.realhomehunt.util.kt.filterNotOpenedWorld
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.submit
import java.util.function.Consumer

internal object ShowOnlyTargetBlockTask {
    @Awake(LifeCycle.ACTIVE)
    fun setup() {
        submit(async = true, delay = 20, period = 1) {
            if (Config.showOnlyTargetBlock) {
                Bukkit.getOnlinePlayers()
                    .filterNotActive()
                    .filterNotOpenedWorld()
                    .forEach {
                        val itemStack = it.inventory.itemInMainHand
                        if (itemStack == null || itemStack.type.isAir) return@forEach
                        val range = Guns.getDistance(itemStack)
                        if (range <= 0.0) return@forEach
                        val rayTraceResult = RayTraceUtil.rayTraceBlock(it, range)
                        val damageCachePoolMap = Processors.GUN_HIT_BLOCK_PROCESSOR.damageCachePoolMap
                        if (!damageCachePoolMap.containsKey(it.uniqueId)) return@forEach
                        val damageCachePool = damageCachePoolMap[it.uniqueId]
                        if (rayTraceResult.isEmpty) {
                            damageCachePool!!.damageCaches.forEach { cache ->
                                cache.healthBossBar.hide(
                                    it
                                )
                            }
                        }
                        RayTraceUtil.rayTraceBlock(it, range).ifPresent { result: Block ->
                            damageCachePool!!.getDamageCacheWithoutBlock(result)
                                .forEach { cache -> cache.healthBossBar.hide(it) }
                            damageCachePool.getDamageCacheByBlockOrEmpty(result)
                                .ifPresent { cache -> cache.healthBossBar.show(it) }
                        }
                    }
            }
        }
    }
}
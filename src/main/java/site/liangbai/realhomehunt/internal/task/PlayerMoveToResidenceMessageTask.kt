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
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager
import site.liangbai.realhomehunt.util.Locations
import site.liangbai.realhomehunt.util.kt.filterNotActive
import site.liangbai.realhomehunt.util.kt.filterNotOpenedWorld
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.submit
import taboolib.platform.util.sendLang
import java.util.concurrent.ConcurrentHashMap

internal object PlayerMoveToResidenceMessageTask {
    private val moveToResidenceCache: MutableMap<String, String> = ConcurrentHashMap()

    @Awake(LifeCycle.ACTIVE)
    fun setup() {
        submit(async = true, delay = 20, period = 1) {
            Bukkit.getOnlinePlayers()
                .filterNotActive()
                .filterNotOpenedWorld()
                .forEach {
                    val name = it.name
                    val location = Locations.toBlockLocation(it.location)
                    val residence = ResidenceManager.getResidenceByLocation(location)
                    if (residence == null) {
                        if (name in moveToResidenceCache) {
                            val other = moveToResidenceCache[name]!!
                            it.sendLang("action-residence-move-out", other)
                            moveToResidenceCache.remove(name)
                        }
                    } else {
                        val lastResidence = moveToResidenceCache[name]
                        if (residence.owner != lastResidence) {
                            val other = residence.owner
                            if (lastResidence != null) {
                                it.sendLang("action-residence-move-out", lastResidence)
                            }

                            it.sendLang("action-residence-move-in", other)
                            moveToResidenceCache[name] = other
                        }
                    }
                }
        }
    }
}
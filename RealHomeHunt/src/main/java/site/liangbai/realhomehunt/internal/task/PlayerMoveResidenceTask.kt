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
import site.liangbai.realhomehunt.api.event.residence.AsyncPlayerMoveChangedResidenceEvent
import site.liangbai.realhomehunt.api.event.residence.AsyncPlayerMoveInResidenceEvent
import site.liangbai.realhomehunt.api.event.residence.AsyncPlayerMoveOutResidenceEvent
import site.liangbai.realhomehunt.api.residence.Residence
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager
import site.liangbai.realhomehunt.util.Locations
import site.liangbai.realhomehunt.util.kt.filterNotActive
import site.liangbai.realhomehunt.util.kt.filterNotOpenedWorld
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.submit
import java.util.concurrent.ConcurrentHashMap

internal object PlayerMoveResidenceTask {
    private val moveToResidenceCache: MutableMap<String, Residence> = ConcurrentHashMap()

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
                    if (residence == null && name in moveToResidenceCache) {
                        AsyncPlayerMoveOutResidenceEvent(it, moveToResidenceCache[name]!!)
                            .post().run { moveToResidenceCache.remove(name) }
                        //
                    } else if (residence != null) {
                        val lastResidence = moveToResidenceCache[name]
                        if (residence.owner != lastResidence?.owner) {
                            if (lastResidence != null) {
                                AsyncPlayerMoveOutResidenceEvent(it, lastResidence).post()
                                AsyncPlayerMoveChangedResidenceEvent(it, residence, lastResidence).post()
                            }
                            AsyncPlayerMoveInResidenceEvent(it, residence).post()

                            moveToResidenceCache[name] = residence
                        }
                    }
                }
        }
    }
}
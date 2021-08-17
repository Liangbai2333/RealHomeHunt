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

package site.liangbai.realhomehunt.internal.listener.entity.living

import org.bukkit.entity.Animals
import org.bukkit.entity.Monster
import org.bukkit.event.entity.CreatureSpawnEvent
import site.liangbai.realhomehunt.api.residence.attribute.impl.AnimalsAttribute
import site.liangbai.realhomehunt.api.residence.attribute.impl.CreatureAttribute
import site.liangbai.realhomehunt.api.residence.attribute.impl.MonsterAttribute
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager
import taboolib.common.platform.event.SubscribeEvent

internal object ListenerCreatureSpawn {
    @SubscribeEvent
    fun onCreatureSpawn(event: CreatureSpawnEvent) {
        val entity = event.entity
        val spawnLocation = event.location
        val residence = ResidenceManager.getResidenceByLocation(spawnLocation)
        if (residence != null) {
            if (!residence.checkBooleanAttribute(CreatureAttribute::class.java)) {
                event.isCancelled = true
                return
            }
            if (entity is Monster && !residence.checkBooleanAttribute(MonsterAttribute::class.java)) {
                event.isCancelled = true
                return
            }
            if (entity is Animals && !residence.checkBooleanAttribute(AnimalsAttribute::class.java)) {
                event.isCancelled = true
            }
        }
    }
}
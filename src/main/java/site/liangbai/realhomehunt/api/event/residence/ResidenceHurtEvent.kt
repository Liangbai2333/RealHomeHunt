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

package site.liangbai.realhomehunt.api.event.residence

import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import site.liangbai.realhomehunt.api.cache.DamageCachePool.DamageCache
import site.liangbai.realhomehunt.api.event.EventCancellable
import site.liangbai.realhomehunt.api.residence.Residence

class ResidenceHurtEvent(
    val source: Player,
    val residence: Residence,
    val hitBlock: Block,
    val gun: ItemStack,
    val damageCache: DamageCache,
    blockHardness: Double
) : EventCancellable<ResidenceHurtEvent?>() {
    val hitPos: Location
        get() = hitBlock.location.clone()
    var blockHardness: Double
        get() = damageCache.hardness
        set(blockHardness) {
            damageCache.hardness = blockHardness
        }
}
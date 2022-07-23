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

package site.liangbai.realhomehunt.util

import com.craftingdead.core.world.item.GunItem
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import site.liangbai.realhomehunt.common.config.Config
import site.liangbai.realhomehunt.util.kt.asItem
import site.liangbai.realhomehunt.util.kt.toMinecraftItemStack

object Guns {
    fun countBlockSit(count: Double, hardness: Double): Int {
        val realHardness = (getHardnessMix(count, hardness) * 9).toInt()
        return if (realHardness > 9 || realHardness < 0) -1 else realHardness
    }

    fun getHardnessMix(count: Double, hardness: Double): Double {
        return count / hardness
    }

    fun getHardnessMixPercent(count: Double, hardness: Double): Int {
        var percent = (getHardnessMix(count, hardness) * 100).toInt()
        if (percent < 0) {
            percent = 0
        } else if (percent > 100) {
            percent = 100
        }
        return percent
    }

    fun countDamage(gun: ItemStack): Double {
        return gun.withGun {
            val powerLevel = gun.getEnchantmentLevel(Enchantment.ARROW_DAMAGE)
            if (it == null) {
                0.0
            } else {
                Config.gun.custom.getCustomDamage(gun.type, it.asGun().damage.toDouble(), powerLevel)
            }
        }
    }



    fun getDistance(gun: ItemStack):  Double {
        return gun.withGun {
            it?.asGun()?.range ?: 0.0
        }
    }

    fun <R> ItemStack.withGun(func: (GunItem?) -> R): R {
        val itemStack = toMinecraftItemStack()

        val item = itemStack.asItem()
        return if (item is GunItem) {
            func(item)
        } else func(null)
    }
}
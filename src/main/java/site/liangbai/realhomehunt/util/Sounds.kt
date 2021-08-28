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

package site.liangbai.realhomehunt.util

import org.bukkit.Sound
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit

object Sounds {
    fun playSound(player: Player, sound: Sound, count: Int, delaySeconds: Double) {
        var alreadyCount = 0

        submit(period = (delaySeconds * 20).toLong()) {
            if (alreadyCount >= count) {
                cancel()
                return@submit
            }
            player.playSound(player.location, sound, 1.0f, 1.0f)
            alreadyCount++
        }
    }

    @JvmStatic
    fun playDragonAmbientSound(player: Player, count: Int, delaySeconds: Double) {
        playSound(player, Sound.ENTITY_ENDER_DRAGON_AMBIENT, count, delaySeconds)
    }

    @JvmStatic
    fun playLevelUpSound(player: Player, count: Int, delaySeconds: Double) {
        playSound(player, Sound.ENTITY_PLAYER_LEVELUP, count, delaySeconds)
    }
}
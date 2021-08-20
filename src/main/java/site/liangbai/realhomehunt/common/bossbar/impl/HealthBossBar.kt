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

package site.liangbai.realhomehunt.common.bossbar.impl

import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player
import site.liangbai.realhomehunt.common.bossbar.IBossBar

class HealthBossBar(title: String, private val greenToYellowMix: Int, private val yellowToRedMix: Int) : IBossBar {
    private val bossBar: BossBar

    override fun update(current: Int) {
        if (current in yellowToRedMix..greenToYellowMix) {
            bossBar.color = BarColor.YELLOW
        } else if (current in 0..yellowToRedMix) {
            bossBar.color = BarColor.RED
        }
        if (current <= 0) {
            bossBar.progress = 0.0
            hide()
            return
        }
        bossBar.progress = current.toDouble() / 100
    }

    override fun show(player: Player) {
        bossBar.addPlayer(player)
    }

    override fun hide(player: Player) {
        bossBar.removePlayer(player)
    }

    override fun hide() {
        bossBar.removeAll()
        bossBar.isVisible = false
    }

    override fun getHandle() = bossBar

    init {
        check(greenToYellowMix >= yellowToRedMix) { "Error setting for HealthBossBar." }
        bossBar = Bukkit.createBossBar(title, BarColor.GREEN, BarStyle.SOLID)
        bossBar.progress = 1.0
        bossBar.isVisible = true
    }
}
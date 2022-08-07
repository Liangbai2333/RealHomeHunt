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

package site.liangbai.realhomehunt.api.residence.util

import org.bukkit.entity.Player
import site.liangbai.realhomehunt.RealHomeHuntPlugin
import site.liangbai.realhomehunt.api.residence.Residence
import site.liangbai.realhomehunt.common.config.Config
import site.liangbai.realhomehunt.util.Sounds
import site.liangbai.realhomehunt.util.sendLang
import taboolib.common.platform.function.submit

object WarningTools {
    private val bufferingMap = mutableMapOf<Residence, Boolean>()

    var Residence.buffering: Boolean
        get() = bufferingMap.computeIfAbsent(this) { false }
        set(value) {
            bufferingMap[this] = value
        }

    fun Residence.tryWarn(sender: Player) {
        buffering = true
        onlineMembers.forEach {
            it.sendLang("action-warn-title", sender.name)
            Sounds.playLevelUpSound(it, 3, 0.5)
        }

        submit(delay = Config.unloadWarnMills) {
            buffering = false
        }
    }
}
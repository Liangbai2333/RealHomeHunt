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

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import site.liangbai.realhomehunt.api.residence.Residence
import site.liangbai.realhomehunt.common.actionbar.impl.DynamicActionBar
import site.liangbai.realhomehunt.common.config.Config
import site.liangbai.realhomehunt.util.Sounds
import site.liangbai.realhomehunt.util.asLangText
import site.liangbai.realhomehunt.util.sendLang
import site.liangbai.realhomehunt.util.sendToAll
import taboolib.common.platform.function.submit

object AttackTools {
    private val attackers = mutableMapOf<Residence, MutableList<String>>()

    private fun Residence.findAttackers() = attackers
        .computeIfAbsent(this) {
            mutableListOf()
        }

    fun Residence.attackBy(attacker: Player) {
        val ownerPlayer = Bukkit.getPlayerExact(owner)
        Sounds.playDragonAmbientSound(attacker, 1, 0.0)
        ownerPlayer?.let {
            Sounds.playDragonAmbientSound(it, 1, 0.0)
            it.sendLang("action-hit-block-self-title", attacker.name)
            if (Config.showActionBar) {
                val message = it.asLangText("action-hit-block-self-action-bar", attacker.name)
                val actionBar =
                    DynamicActionBar(message, 5, 20)
                actionBar.show(it, Config.actionBarShowMills)
            }
        }
        sendToAll("action-hit-block-all-message", owner, attacker.name)
        addAttack(attacker.name)
    }

    fun Residence.addAttack(attack: String) {
        findAttackers().add(attack)
        submit(delay = Config.unloadPlayerAttackMills) {
            removeAttack(attack)
        }
    }

    fun Residence.hasAttack(attack: String): Boolean {
        return findAttackers().contains(attack)
    }

    fun Residence.removeAttack(attack: String) {
        findAttackers().remove(attack)
    }
}
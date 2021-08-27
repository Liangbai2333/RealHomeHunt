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

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import site.liangbai.realhomehunt.RealHomeHuntPlugin.inst
import site.liangbai.realhomehunt.util.Locations
import site.liangbai.realhomehunt.util.Players
import taboolib.platform.util.sendLang

class PlayerBackTask(
    private val player: Player,
    private val owner: String,
    private val location: Location,
    private val seconds: Long,
    private val finishBlock: Player.() -> Unit
) : BukkitRunnable() {
    private var cancel = false
    private var count = 0
    override fun run() {
        if (count < seconds) {
            player.sendLang("command-back-teleport-wait", owner, seconds - count)
            count++
            return
        }
        cancel = true
        Locations.teleportAfterChunkLoaded(player, location)
        player.finishBlock()
        player.sendLang("command-back-teleport-done", owner)
        cancel()
    }

    companion object {
        fun tryTeleportPlayer(
            player: Player,
            owner: String,
            location: Location,
            seconds: Long,
            finishBlock: Player.() -> Unit
        ) {
            PlayerBackTask(
                player,
                owner,
                location,
                seconds,
                finishBlock
            ).runTaskTimer(
                inst, 0, 20
            )
        }
    }

    init {
        Players.addListenerOnce {
            if (it == player) {
                if (cancel) return@addListenerOnce true
                cancel()
                player.finishBlock()
                player.sendLang("command-back-teleport-deny", owner)
                return@addListenerOnce true
            }
            cancel
        }
    }
}
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

package site.liangbai.realhomehunt.internal.listener.player.residence

import site.liangbai.realhomehunt.api.event.residence.AsyncPlayerMoveInResidenceEvent
import site.liangbai.realhomehunt.api.event.residence.AsyncPlayerMoveOutResidenceEvent
import site.liangbai.realhomehunt.api.residence.attribute.impl.Glow
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.util.sendLang
import java.util.*
import java.util.concurrent.CopyOnWriteArraySet

object ListenerAsyncPlayerMoveResidence {
    private val glowing: MutableSet<UUID> = CopyOnWriteArraySet()

    @SubscribeEvent
    fun onGlowingIn(event: AsyncPlayerMoveInResidenceEvent) {
        val residence = event.residence
        val player = event.player
        when (residence.checkBooleanAttribute<Glow>()) {
            true -> {
                player.takeUnless { player.isGlowing }?.run { player.isGlowing = true; glowing.add(player.uniqueId) }
            }
            false -> {
                player.takeIf { player.isGlowing }?.run { player.isGlowing = false; glowing.remove(player.uniqueId) }
            }
        }
    }

    @SubscribeEvent
    fun onGlowingOut(event: AsyncPlayerMoveOutResidenceEvent) {
        val player = event.player
        glowing.remove(player.uniqueId).takeIf { pred -> pred }?.apply { player.isGlowing = false }
    }

    @SubscribeEvent
    fun onMoveIn(event: AsyncPlayerMoveInResidenceEvent) {
        event.player.sendLang("action-residence-move-in", event.residence.owner)
    }

    @SubscribeEvent
    fun onMoveOut(event: AsyncPlayerMoveOutResidenceEvent) {
        event.player.sendLang("action-residence-move-out", event.residence.owner)
    }
}
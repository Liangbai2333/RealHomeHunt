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

package site.liangbai.realhomehunt.api.event

import org.bukkit.Bukkit
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import java.util.function.Consumer

@Suppress("UNCHECKED_CAST")
abstract class EventCallable<T : EventCallable<T>?>(async: Boolean = false) : Event(async) {
    fun post(): Boolean {
        return if (call() is Cancellable) {
            !(this as Cancellable).isCancelled
        } else {
            true
        }
    }

    fun call(): T {
        Bukkit.getPluginManager().callEvent(this)
        return this as T
    }

    fun then(block: Consumer<T>): T {
        block.accept(this as T)
        return this
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}
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

import org.bukkit.event.Cancellable
import java.util.function.Consumer

@Suppress("UNCHECKED_CAST")
abstract class EventCancellable<T : EventCallable<T>?>(async: Boolean = false) : EventCallable<T>(async), Cancellable {
    private var cancelled = false
    fun ifCancelled(block: Consumer<T>?): T? {
        return if (isCancelled) {
            then(block!!)
        } else this as T
    }

    fun nonCancelled(block: Consumer<T>?): T? {
        return if (!isCancelled) {
            then(block!!)
        } else this as T
    }

    override fun isCancelled(): Boolean {
        return cancelled
    }

    override fun setCancelled(cancelled: Boolean) {
        this.cancelled = cancelled
    }
}
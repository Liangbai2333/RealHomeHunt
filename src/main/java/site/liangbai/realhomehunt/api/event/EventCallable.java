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

package site.liangbai.realhomehunt.api.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public abstract class EventCallable<T extends EventCallable<T>> extends Event {
    private static final HandlerList handlerList = new HandlerList();

    public boolean callEvent() {
        if (call() instanceof Cancellable) {
            return !((Cancellable) this).isCancelled();
        } else {
            return true;
        }
    }

    public T call() {
        Bukkit.getPluginManager().callEvent(this);

        return (T) this;
    }

    public T then(Consumer<T> block) {
        block.accept((T) this);

        return (T) this;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}

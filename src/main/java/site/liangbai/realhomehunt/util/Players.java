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

package site.liangbai.realhomehunt.util;

import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public final class Players {
    private static final List<Predicate<Player>> listenerList = new LinkedList<>();

    public static void addListenerOnce(Predicate<Player> playerPredicate) {
        listenerList.add(playerPredicate);
    }

    public static void push(Player player) {
        listenerList.forEach(it -> {
            if (it.test(player)) {
                listenerList.remove(it);
            }
        });
    }
}

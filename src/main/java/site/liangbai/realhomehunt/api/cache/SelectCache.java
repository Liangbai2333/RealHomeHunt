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

package site.liangbai.realhomehunt.api.cache;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public final class SelectCache {
    private static final Map<String, Location> firstCaches = new HashMap<>();
    private static final Map<String, Location> secondCaches = new HashMap<>();

    public static void push(SelectType selectType, String player, Location location) {
        if (selectType == SelectType.FIRST) {
            firstCaches.put(player, location.clone());
        }

        if (selectType == SelectType.SECOND) {
            secondCaches.put(player, location.clone());
        }
    }

    public static void pop(SelectType selectType, String player) {
        if (selectType == SelectType.FIRST) {
            firstCaches.remove(player);
        }

        if (selectType == SelectType.SECOND) {
            secondCaches.remove(player);
        }
    }

    public static void pop(String player) {
        pop(SelectType.FIRST, player);

        pop(SelectType.SECOND, player);
    }

    public static Location require(SelectType selectType, String player) {
        if (selectType == SelectType.FIRST) {
            return firstCaches.get(player);
        }

        if (selectType == SelectType.SECOND) {
            return secondCaches.get(player);
        }

        return null;
    }

    public enum SelectType {
        FIRST,
        SECOND
    }
}
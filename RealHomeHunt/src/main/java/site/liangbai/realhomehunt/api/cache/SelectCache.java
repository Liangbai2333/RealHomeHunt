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

package site.liangbai.realhomehunt.api.cache;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import site.liangbai.realhomehunt.common.config.Config;
import site.liangbai.realhomehunt.common.particle.EffectGroup;
import site.liangbai.realhomehunt.util.Zones;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SelectCache {
    private static final Map<String, Location> firstCaches = new HashMap<>();
    private static final Map<String, Location> secondCaches = new HashMap<>();

    private static final Map<String, EffectGroup> showCaches = new ConcurrentHashMap<>();

    public static void push(SelectType selectType, Player player, Location location) {
        if (selectType == SelectType.FIRST) {
            firstCaches.put(player.getName(), location.clone());
        }

        if (selectType == SelectType.SECOND) {
            secondCaches.put(player.getName(), location.clone());
        }

        updatePlayerShowSelectZone(player);
    }

    public static void pop(SelectType selectType, Player player) {
        if (selectType == SelectType.FIRST) {
            firstCaches.remove(player.getName());
        }

        if (selectType == SelectType.SECOND) {
            secondCaches.remove(player.getName());
        }

        updatePlayerShowSelectZone(player);
    }

    public static void pop(Player player) {
        pop(SelectType.FIRST, player);

        pop(SelectType.SECOND, player);
    }

    public static void updatePlayerShowSelectZone(Player player) {
        if (!Config.residence.tool.showSelectZone) return;

        String name = player.getName();

        Location first = require(SelectType.FIRST, name);
        Location second = require(SelectType.SECOND, name);

        if (first != null && second != null && !first.equals(second) && player.getWorld().equals(first.getWorld())) {
            if (showCaches.containsKey(name)) {
                EffectGroup effectGroup = showCaches.get(name);

                effectGroup.turnOff();

                showCaches.remove(name);
            }

            showCaches.put(name, Zones.startShowWithBlockLocation(player, first, second));

        } else if (showCaches.containsKey(name)) {
            EffectGroup effectGroup = showCaches.get(name);

            effectGroup.turnOff();

            showCaches.remove(name);
        }
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

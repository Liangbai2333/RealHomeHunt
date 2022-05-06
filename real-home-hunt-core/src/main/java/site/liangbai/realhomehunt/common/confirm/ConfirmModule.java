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

package site.liangbai.realhomehunt.common.confirm;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import site.liangbai.realhomehunt.RealHomeHuntPlugin;
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager;
import site.liangbai.realhomehunt.api.residence.Residence;
import site.liangbai.realhomehunt.util.LangBridge;
import site.liangbai.realhomehunt.util.Pair;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ConfirmModule {
    private static final Map<UUID, Pair<Residence, IConfirmProcessor>> confirmCacheMap = new ConcurrentHashMap<>();

    public static void pushConfirmCache(Player player, long waitMills, IConfirmProcessor confirmProcessor) {
        pushConfirmCache(player, ResidenceManager.getResidenceByOwner(player.getName()), waitMills, confirmProcessor);
    }

    public static void pushConfirmCache(Player player, Residence residence, long waitMills, IConfirmProcessor confirmProcessor) {
        confirmCacheMap.put(player.getUniqueId(), new Pair<>(residence, confirmProcessor));

        new BukkitRunnable() {

            @Override
            public void run() {
                popConfirmCache(player);
            }
        }.runTaskLater(RealHomeHuntPlugin.INSTANCE.getInst(), waitMills);
    }

    public static void sendPrepare(Player player, String label, long waitMills) {
        LangBridge.sendLang(player, "command-confirm-prepare", label, waitMills / 20);
    }

    public static void popConfirmCache(Player player) {
        confirmCacheMap.remove(player.getUniqueId());
    }

    public static boolean hasConfirmCache(Player player) {
        return confirmCacheMap.containsKey(player.getUniqueId());
    }

    public static Pair<Residence, IConfirmProcessor> getConfirmProcessor(Player player) {
        return confirmCacheMap.get(player.getUniqueId());
    }
}

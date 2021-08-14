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

package site.liangbai.realhomehunt.internal.task;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import site.liangbai.realhomehunt.api.cache.DamageCachePool;
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager;
import site.liangbai.realhomehunt.common.config.Config;
import site.liangbai.realhomehunt.internal.processor.Processors;
import site.liangbai.realhomehunt.util.Guns;
import site.liangbai.realhomehunt.util.RayTraceUtil;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class ShowOnlyTargetBlockTask extends BukkitRunnable {

    public static void setup(Plugin plugin) {
        new ShowOnlyTargetBlockTask().runTaskTimerAsynchronously(plugin, 1, 1);
    }

    @Override
    public void run() {
        if (Config.showOnlyTargetBlock) {
            Bukkit.getOnlinePlayers().stream()
                    .filter(it -> ResidenceManager.isOpened(it.getWorld()))
                    .forEach(player -> {
                        ItemStack itemStack = player.getInventory().getItemInMainHand();

                        if (itemStack == null  || itemStack.getType().isAir()) return;

                        double range = Guns.getDistance(itemStack);

                        if (range <= 0.0D) return;

                        Optional<Block> rayTraceResult = RayTraceUtil.rayTraceBlock(player, range);

                        Map<UUID, DamageCachePool> damageCachePoolMap = Processors.GUN_HIT_BLOCK_PROCESSOR.getDamageCachePoolMap();

                        if (!damageCachePoolMap.containsKey(player.getUniqueId())) return;

                        DamageCachePool damageCachePool = damageCachePoolMap.get(player.getUniqueId());

                        if (rayTraceResult.isEmpty()) {
                            damageCachePool.getDamageCaches().forEach(it -> it.getHealthBossBar().hide(player));
                        }

                        RayTraceUtil.rayTraceBlock(player, range).ifPresent(result -> {
                            damageCachePool.getDamageCacheWithoutBlock(result).forEach(it -> it.getHealthBossBar().hide(player));

                            damageCachePool.getDamageCacheByBlockOrEmpty(result).ifPresent(damageCache -> damageCache.getHealthBossBar().show(player));
                        });
                    });
        }
    }
}

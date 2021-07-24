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

package site.liangbai.realhomehunt.task;

import org.bukkit.scheduler.BukkitRunnable;
import site.liangbai.realhomehunt.cache.DamageCachePool;
import site.liangbai.realhomehunt.util.Blocks;

public final class UnloadDamageCacheTask extends BukkitRunnable {
    private final DamageCachePool damageCachePool;

    private final DamageCachePool.DamageCache damageCache;

    public UnloadDamageCacheTask(DamageCachePool damageCachePool, DamageCachePool.DamageCache damageCache) {
        this.damageCachePool = damageCachePool;
        this.damageCache = damageCache;

        damageCache.updateOnce(it -> cancel());
    }

    @Override
    public void run() {
        Blocks.sendClearBreakAnimationPacket(damageCache.getId(), damageCache.getBlock());

        damageCache.getHealthBossBar().hide();

        damageCachePool.removeDamageCache(damageCache);
    }
}

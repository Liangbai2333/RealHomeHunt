package site.liangbai.realhomehunt.task;

import org.bukkit.scheduler.BukkitRunnable;
import site.liangbai.realhomehunt.cache.DamageCachePool;
import site.liangbai.realhomehunt.util.BlockUtil;

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
        BlockUtil.sendClearBreakAnimationPacket(damageCache.getId(), damageCache.getBlock());

        damageCachePool.removeDamageCache(damageCache);
    }
}

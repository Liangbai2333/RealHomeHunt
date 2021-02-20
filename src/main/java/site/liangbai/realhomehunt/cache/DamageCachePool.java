package site.liangbai.realhomehunt.cache;

import org.bukkit.block.Block;
import site.liangbai.realhomehunt.util.BlockUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public final class DamageCachePool {
    private final List<DamageCache> damageCaches = new ArrayList<>();

    public void addDamageCache(DamageCache damageCache) {
        if (!damageCaches.contains(damageCache)) damageCaches.add(damageCache);
    }

    public void removeDamageCache(DamageCache damageCache) {
        damageCaches.remove(damageCache);
    }

    public DamageCache getDamageCacheByBlock(Block block) {
        for (DamageCache damageCache : damageCaches) {
            if (damageCache.getBlock().equals(block)) return damageCache;
        }

        return new DamageCache(block, BlockUtil.nextId());
    }

    public static final class DamageCache {
        private final Block block;

        private double damage;

        private final int id;

        private final List<Consumer<DamageCache>> consumers = new LinkedList<>();

        public DamageCache(Block block, int id) {
            this.block = block;

            this.id = id;
        }

        public Block getBlock() {
            return block;
        }

        public void increaseDamage(double damage) {
            this.damage += damage;

            consumers.forEach(it -> {
                it.accept(this);

                consumers.remove(it);
            });
        }

        public int getId() {
            return id;
        }

        public void updateOnce(Consumer<DamageCache> consumer) {
            consumers.add(consumer);
        }

        public double getDamage() {
            return damage;
        }
    }
}

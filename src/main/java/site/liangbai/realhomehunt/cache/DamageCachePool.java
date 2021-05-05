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

package site.liangbai.realhomehunt.cache;

import org.bukkit.block.Block;
import site.liangbai.realhomehunt.util.Blocks;

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

        return new DamageCache(block, Blocks.nextId());
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

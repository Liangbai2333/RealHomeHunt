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

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import site.liangbai.realhomehunt.api.residence.Residence;
import site.liangbai.realhomehunt.common.bossbar.IBossBar;
import site.liangbai.realhomehunt.util.Blocks;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class DamageCachePool {
    private final List<DamageCache> damageCaches = new ArrayList<>();

    public void addDamageCache(DamageCache damageCache) {
        if (!damageCaches.contains(damageCache)) damageCaches.add(damageCache);
    }

    public List<DamageCache> getDamageCaches() {
        return damageCaches;
    }

    public void removeDamageCache(DamageCache damageCache) {
        damageCaches.remove(damageCache);
    }

    public Optional<DamageCache> getDamageCacheByBlockOrEmpty(Block block) {
        return damageCaches.stream()
                .filter(it -> it.getBlock().equals(block))
                .findFirst();
    }

    public List<DamageCache> getDamageCacheWithoutBlock(Block block) {
        return damageCaches.stream()
                .filter(it -> !it.getBlock().equals(block))
                .collect(Collectors.toList());
    }

    public DamageCache getDamageCacheByBlock(Player operator, Residence residence, Block block, Supplier<Double> hardness, Supplier<IBossBar> nonBossBar) {
        return damageCaches.stream()
                .filter(it -> it.getBlock().equals(block))
                .findFirst()
                .orElseGet(() -> {
                    DamageCache damageCache = new DamageCache(this, operator, residence, block, hardness.get(), Blocks.nextId(), nonBossBar.get());

                    damageCaches.add(damageCache);

                    return damageCache;
                });
    }

    public static final class DamageCache {
        private final DamageCachePool parent;

        private final Player operator;
        private final Residence residence;
        private final Block block;
        private final IBossBar healthBossBar;
        private double damage;
        private double hardness;
        private final int id;
        private final List<Consumer<DamageCache>> consumers = new LinkedList<>();

        public DamageCache(DamageCachePool parent, Player operator, Residence residence, Block block, double hardness, int id, IBossBar healthBossBar) {
            this.parent = parent;
            this.operator = operator;
            this.residence = residence;
            this.block = block;
            this.hardness = hardness;
            this.id = id;
            this.healthBossBar = healthBossBar;
        }

        public DamageCachePool getParent() {
            return parent;
        }

        public Player getOperator() {
            return operator;
        }

        public Residence getResidence() {
            return residence;
        }

        public IBossBar getHealthBossBar() {
            return healthBossBar;
        }

        public Block getBlock() {
            return block;
        }

        public double getHardness() {
            return hardness;
        }

        public void setHardness(double hardness) {
            this.hardness = hardness;
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

        public void setDamage(double damage) {
            this.damage = damage;
        }
    }
}

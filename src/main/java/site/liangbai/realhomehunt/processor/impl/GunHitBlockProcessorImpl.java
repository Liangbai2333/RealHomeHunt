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

package site.liangbai.realhomehunt.processor.impl;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import site.liangbai.realhomehunt.RealHomeHunt;
import site.liangbai.realhomehunt.bossbar.IBossBar;
import site.liangbai.realhomehunt.bossbar.factory.BossBarFactory;
import site.liangbai.realhomehunt.cache.DamageCachePool;
import site.liangbai.realhomehunt.config.Config;
import site.liangbai.realhomehunt.listener.player.block.ListenerBlockBreak;
import site.liangbai.realhomehunt.locale.impl.Locale;
import site.liangbai.realhomehunt.locale.manager.LocaleManager;
import site.liangbai.realhomehunt.manager.ResidenceManager;
import site.liangbai.realhomehunt.processor.IGunHitBlockProcessor;
import site.liangbai.realhomehunt.residence.Residence;
import site.liangbai.realhomehunt.task.AutoFixBlockTask;
import site.liangbai.realhomehunt.task.UnloadDamageCacheTask;
import site.liangbai.realhomehunt.util.Blocks;
import site.liangbai.realhomehunt.util.Guns;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GunHitBlockProcessorImpl implements IGunHitBlockProcessor {
    private final Map<UUID, DamageCachePool> damageCachePoolMap = new HashMap<>();

    @Override
    public void processGunHitBlock(Player player, ItemStack gun, Block block) {
        if (!ResidenceManager.isOpened(player.getWorld())) return;

        if (gun == null) return;

        if (!damageCachePoolMap.containsKey(player.getUniqueId())) damageCachePoolMap.put(player.getUniqueId(), new DamageCachePool());

        if (Config.block.ignore.contains(block.getType())) return;

        Residence residence = ResidenceManager.getResidenceByLocation(block.getLocation());

        if (residence == null || residence.isAdministrator(player)) return;

        DamageCachePool damageCachePool = damageCachePoolMap.get(player.getUniqueId());

        Locale locale = LocaleManager.require(player);

        DamageCachePool.DamageCache damageCache = damageCachePool.getDamageCacheByBlock(block, () -> {
            String title = locale.asString("action.hitBlock.performer.bossBar", residence.getOwner(), 0, 0);

            return BossBarFactory.newHealthBossBar(title, 70, 30);
        });

        damageCachePool.addDamageCache(damageCache);

        damageCache.increaseDamage(Guns.countDamage(gun));

        double hardness = Config.block.custom.getHardness(block);

        if (hardness <= 0) return;

        IBossBar healthBossBar = damageCache.getHealthBossBar();

        if (Config.showBlockHealth) {
            healthBossBar.show(player);
        }

        if (damageCache.getDamage() >= hardness) {
            Blocks.sendClearBreakAnimationPacket(damageCache.getId(), damageCache.getBlock());

            healthBossBar.update(0);

            healthBossBar.getHandle().setTitle(locale.asString("action.hitBlock.performer.bossBar", residence.getOwner(), 0, (int) hardness));

            Block upBlock = block.getRelative(BlockFace.UP);

            if (!upBlock.getType().isAir()) {
                ListenerBlockBreak.saveUpBlock(upBlock, residence);
            }

            BlockData blockData = damageCache.getBlock().getBlockData().clone();

            Blocks.sendBreakBlockPacket(damageCache.getBlock(), Config.dropItem);

            damageCachePool.removeDamageCache(damageCache);

            if (Config.autoFixResidence.enabled) {
                AutoFixBlockTask.submit(residence, blockData, damageCache.getBlock().getLocation().clone());
            }
        } else {
            int blockSit = Guns.countBlockSit(damageCache.getDamage(), hardness);

            healthBossBar.update(100 - Guns.getHardnessMixPercent(damageCache.getDamage(), hardness));

            double health = hardness - damageCache.getDamage();

            String healthString = String.format("%.1f", health);

            healthBossBar.getHandle().setTitle(locale.asString("action.hitBlock.performer.bossBar", residence.getOwner(), healthString, (int) hardness));

            Blocks.sendBreakAnimationPacket(damageCache.getId(), damageCache.getBlock(), blockSit);

            new UnloadDamageCacheTask(damageCachePool, damageCache).runTaskLater(RealHomeHunt.plugin, Config.maxWaitMills);
        }

        if (!residence.hasAttack(player.getName())) {
            residence.attackBy(player);
        }
    }
}

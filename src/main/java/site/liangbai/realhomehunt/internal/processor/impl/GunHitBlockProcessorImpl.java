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

package site.liangbai.realhomehunt.internal.processor.impl;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import site.liangbai.realhomehunt.RealHomeHuntPlugin;
import site.liangbai.realhomehunt.api.event.residence.ResidenceHurtEvent;
import site.liangbai.realhomehunt.common.bossbar.IBossBar;
import site.liangbai.realhomehunt.common.bossbar.factory.BossBarFactory;
import site.liangbai.realhomehunt.api.cache.DamageCachePool;
import site.liangbai.realhomehunt.common.config.Config;
import site.liangbai.realhomehunt.api.gamemode.manager.GameModeManager;
import site.liangbai.realhomehunt.internal.listener.player.block.ListenerBlockBreak;
import site.liangbai.realhomehunt.api.locale.impl.Locale;
import site.liangbai.realhomehunt.api.locale.manager.LocaleManager;
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager;
import site.liangbai.realhomehunt.internal.processor.IGunHitBlockProcessor;
import site.liangbai.realhomehunt.api.residence.Residence;
import site.liangbai.realhomehunt.internal.task.UnloadDamageCacheTask;
import site.liangbai.realhomehunt.util.Blocks;
import site.liangbai.realhomehunt.util.Guns;
import site.liangbai.realhomehunt.util.callback.ICallback;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The type Gun hit block processor.
 *
 * @author Liangbai
 * @since 2021 /08/11 02:48 下午
 */
public class GunHitBlockProcessorImpl implements IGunHitBlockProcessor {
    private static final Map<UUID, DamageCachePool> DAMAGE_CACHE_POOL_MAP = new HashMap<>();

    @Override
    public void processGunHitBlock(Player player, ItemStack gun, Block block) {
        if (!ResidenceManager.isOpened(player.getWorld())) return;

        if (gun == null) return;

        if (!DAMAGE_CACHE_POOL_MAP.containsKey(player.getUniqueId())) DAMAGE_CACHE_POOL_MAP.put(player.getUniqueId(), new DamageCachePool());

        if (Config.block.ignore.isIgnoreHit(block.getType())) return;

        Residence residence = ResidenceManager.getResidenceByLocation(block.getLocation());

        if (residence == null || residence.isAdministrator(player)) return;

        DamageCachePool damageCachePool = DAMAGE_CACHE_POOL_MAP.get(player.getUniqueId());

        Locale locale = LocaleManager.require(player);

        DamageCachePool.DamageCache damageCache = damageCachePool.getDamageCacheByBlock(block,
                () -> Config.block.custom.getHardness(block),
                () -> {
            String title = locale.asString("action.hitBlock.performer.bossBar", residence.getOwner(), 0, 0);

            return BossBarFactory.newHealthBossBar(title, 70, 30);
        });

        if (!new ResidenceHurtEvent(player, residence, block, gun, damageCache, damageCache.getHardness()).callEvent()) return;

        if (damageCache.getHardness() <= 0) return;

        damageCache.increaseDamage(Guns.countDamage(gun));

        IBossBar healthBossBar = damageCache.getHealthBossBar();

        if (Config.showBlockHealth && !Config.showOnlyTargetBlock) {
            healthBossBar.show(player);
        }

        if (damageCache.getDamage() >= damageCache.getHardness()) {
            Blocks.sendClearBreakAnimationPacket(damageCache.getId(), damageCache.getBlock());

            healthBossBar.update(0);

            healthBossBar.getHandle().setTitle(locale.asString("action.hitBlock.performer.bossBar", residence.getOwner(), 0, (int) damageCache.getHardness()));

            Block upBlock = block.getRelative(BlockFace.UP);

            if (!upBlock.getType().isAir()) {
                ListenerBlockBreak.saveUpBlock(upBlock, residence);
            }

            BlockData blockData = damageCache.getBlock().getBlockData().clone();

            ICallback<Boolean> callBack = GameModeManager.submit(residence, player, gun, block, block.getState(), blockData);

            Blocks.sendBreakBlockPacket(damageCache.getBlock(), Config.dropItem && callBack.get());

            damageCachePool.removeDamageCache(damageCache);
        } else {
            int blockSit = Guns.countBlockSit(damageCache.getDamage(), damageCache.getHardness());

            healthBossBar.update(100 - Guns.getHardnessMixPercent(damageCache.getDamage(), damageCache.getHardness()));

            double health = damageCache.getHardness() - damageCache.getDamage();

            String healthString = String.format("%.1f", health);

            healthBossBar.getHandle().setTitle(locale.asString("action.hitBlock.performer.bossBar", residence.getOwner(), healthString, (int) damageCache.getHardness()));

            Blocks.sendBreakAnimationPacket(damageCache.getId(), damageCache.getBlock(), blockSit);

            new UnloadDamageCacheTask(damageCachePool, damageCache).runTaskLater(RealHomeHuntPlugin.getInst(), Config.maxWaitMills);
        }

        if (!residence.hasAttack(player.getName())) {
            residence.attackBy(player);
        }
    }

    @Override
    public Map<UUID, DamageCachePool> getDamageCachePoolMap() {
        return DAMAGE_CACHE_POOL_MAP;
    }
}

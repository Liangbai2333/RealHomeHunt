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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import site.liangbai.realhomehunt.RealHomeHunt;
import site.liangbai.realhomehunt.cache.DamageCachePool;
import site.liangbai.realhomehunt.config.Config;
import site.liangbai.realhomehunt.listener.player.block.ListenerBlockBreak;
import site.liangbai.realhomehunt.manager.ResidenceManager;
import site.liangbai.realhomehunt.processor.IGunHitBlockProcessor;
import site.liangbai.realhomehunt.residence.Residence;
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

        DamageCachePool.DamageCache damageCache = damageCachePool.getDamageCacheByBlock(block);

        damageCachePool.addDamageCache(damageCache);

        damageCache.increaseDamage(Guns.countDamage(gun));

        double hardness = Config.block.custom.getHardness(block);

        if (hardness <= 0) return;

        if (damageCache.getDamage() >= hardness) {
            Blocks.sendClearBreakAnimationPacket(damageCache.getId(), damageCache.getBlock());

            Block upBlock = block.getRelative(BlockFace.UP);

            ListenerBlockBreak.saveUpBlock(upBlock, residence);

            Blocks.sendBreakBlockPacket(damageCache.getBlock());

            damageCachePool.removeDamageCache(damageCache);
        } else {
            int blockSit = Guns.countBlockSit(damageCache.getDamage(), hardness);

            Blocks.sendBreakAnimationPacket(damageCache.getId(), damageCache.getBlock(), blockSit);

            new UnloadDamageCacheTask(damageCachePool, damageCache).runTaskLater(RealHomeHunt.plugin, Config.maxWaitMills);
        }

        String attack = player.getName();

        if (!residence.hasAttack(attack)) {
            residence.attackBy(attack);
        }
    }
}

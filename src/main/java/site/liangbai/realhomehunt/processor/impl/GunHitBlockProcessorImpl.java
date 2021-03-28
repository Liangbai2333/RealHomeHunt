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
import site.liangbai.realhomehunt.util.BlockUtil;
import site.liangbai.realhomehunt.util.GunUtil;

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

        damageCache.increaseDamage(GunUtil.countDamage(gun));

        double hardness = Config.block.custom.getHardness(block);

        if (hardness <= 0) return;

        if (damageCache.getDamage() >= hardness) {
            BlockUtil.sendClearBreakAnimationPacket(damageCache.getId(), damageCache.getBlock());

            Block upBlock = block.getRelative(BlockFace.UP);

            ListenerBlockBreak.saveUpBlock(upBlock, residence);

            BlockUtil.sendBreakBlockPacket(damageCache.getBlock());

            damageCachePool.removeDamageCache(damageCache);
        } else {
            int blockSit = GunUtil.countBlockSit(damageCache.getDamage(), hardness);

            BlockUtil.sendBreakAnimationPacket(damageCache.getId(), damageCache.getBlock(), blockSit);

            new UnloadDamageCacheTask(damageCachePool, damageCache).runTaskLater(RealHomeHunt.plugin, Config.maxWaitMills);
        }

        String attack = player.getName();

        if (!residence.hasAttack(attack)) {
            residence.attackBy(attack);
        }
    }
}

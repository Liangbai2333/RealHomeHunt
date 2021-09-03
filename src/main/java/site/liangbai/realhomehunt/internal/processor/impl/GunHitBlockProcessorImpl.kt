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

package site.liangbai.realhomehunt.internal.processor.impl

import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import site.liangbai.realhomehunt.RealHomeHuntPlugin.inst
import site.liangbai.realhomehunt.api.cache.DamageCachePool
import site.liangbai.realhomehunt.api.event.residence.ResidenceHurtEvent
import site.liangbai.realhomehunt.api.gamemode.manager.GameModeManager
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager
import site.liangbai.realhomehunt.common.bossbar.factory.BossBarFactory
import site.liangbai.realhomehunt.common.config.Config
import site.liangbai.realhomehunt.internal.listener.player.block.ListenerBlockBreak.saveUpBlock
import site.liangbai.realhomehunt.internal.processor.IGunHitBlockProcessor
import site.liangbai.realhomehunt.internal.task.UnloadDamageCacheTask
import site.liangbai.realhomehunt.util.Blocks.sendBreakAnimationPacket
import site.liangbai.realhomehunt.util.Blocks.sendBreakBlockPacket
import site.liangbai.realhomehunt.util.Blocks.sendClearBreakAnimationPacket
import site.liangbai.realhomehunt.util.Guns
import site.liangbai.realhomehunt.util.asLangText
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * The type Gun hit block processor.
 *
 * @author Liangbai
 * @since 2021 /08/11 02:48 下午
 */
class GunHitBlockProcessorImpl : IGunHitBlockProcessor {
    override val damageCachePoolMap: Map<UUID, DamageCachePool> = DAMAGE_CACHE_POOL_MAP

    override fun processGunHitBlock(player: Player, gun: ItemStack, block: Block) {
        if (!ResidenceManager.isOpened(player.world)) return
        if (!DAMAGE_CACHE_POOL_MAP.containsKey(player.uniqueId)) DAMAGE_CACHE_POOL_MAP[player.uniqueId] =
            DamageCachePool()
        if (Config.gun.ignore.isIgnored(gun.type)) return
        if (Config.block.ignore.isIgnoreHit(block.type)) return
        val residence = ResidenceManager.getResidenceByLocation(block.location)
        if (residence == null || residence.isAdministrator(player)) return
        val damageCachePool = DAMAGE_CACHE_POOL_MAP[player.uniqueId]
        val damageCache = damageCachePool!!.getDamageCacheByBlock(block,
            { Config.block.custom.getHardness(block) }
        ) {
            val title = player.asLangText("action-hit-block-performer-boss-bar", residence.owner, 0, 0)
            BossBarFactory.newHealthBossBar(title, 70, 30)
        }
        if (!ResidenceHurtEvent(
                player,
                residence,
                block,
                gun,
                damageCache,
                damageCache.hardness
            ).post()
        ) return
        if (damageCache.hardness <= 0) return
        damageCache.increaseDamage(Guns.countDamage(gun))
        val healthBossBar = damageCache.healthBossBar
        if (Config.showBlockHealth && !Config.showOnlyTargetBlock) {
            healthBossBar.show(player)
        }
        if (damageCache.damage >= damageCache.hardness) {
            sendClearBreakAnimationPacket(damageCache.id, damageCache.block)
            healthBossBar.update(0)
            healthBossBar.handle.setTitle(
                player.asLangText(
                    "action-hit-block-performer-boss-bar",
                    residence.owner,
                    0,
                    damageCache.hardness.toInt()
                )
            )
            val upBlock = block.getRelative(BlockFace.UP)
            if (!upBlock.type.isAir) {
                saveUpBlock(upBlock, residence)
            }
            val blockData = damageCache.block.blockData.clone()
            val callBack = GameModeManager.submit(residence, player, gun, block, block.state, blockData)
            sendBreakBlockPacket(damageCache.block, Config.dropItem && callBack.get())
            damageCachePool.removeDamageCache(damageCache)
        } else {
            val blockSit = Guns.countBlockSit(damageCache.damage, damageCache.hardness)
            healthBossBar.update(100 - Guns.getHardnessMixPercent(damageCache.damage, damageCache.hardness))
            val health = damageCache.hardness - damageCache.damage
            val healthString = String.format("%.1f", health)
            healthBossBar.handle.setTitle(
                player.asLangText(
                    "action-hit-block-performer-boss-bar",
                    residence.owner,
                    healthString,
                    damageCache.hardness.toInt()
                )
            )
            sendBreakAnimationPacket(damageCache.id, damageCache.block, blockSit)
            UnloadDamageCacheTask(damageCachePool, damageCache).runTaskLater(inst, Config.maxWaitMills)
        }
        if (!residence.hasAttack(player.name)) {
            residence.attackBy(player)
        }
    }

    companion object {
        private val DAMAGE_CACHE_POOL_MAP: MutableMap<UUID, DamageCachePool> = ConcurrentHashMap()
    }
}
/*
 * RealHomeHunt
 * Copyright (C) 2022  Liangbai
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
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import site.liangbai.realhomehunt.api.cache.DamageCachePool
import site.liangbai.realhomehunt.api.event.residence.AsyncResidenceHurtEvent
import site.liangbai.realhomehunt.api.gamemode.manager.GameModeManager
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager
import site.liangbai.realhomehunt.common.bossbar.factory.BossBarFactory
import site.liangbai.realhomehunt.common.bossbar.impl.HealthBossBar.Companion.clearForHealth
import site.liangbai.realhomehunt.common.bossbar.impl.HealthBossBar.Companion.updateForHealth
import site.liangbai.realhomehunt.common.config.Config
import site.liangbai.realhomehunt.internal.processor.IGunHitBlockProcessor
import site.liangbai.realhomehunt.internal.task.delayUnload
import site.liangbai.realhomehunt.util.Blocks.sendBreakAnimationPacket
import site.liangbai.realhomehunt.util.Blocks.sendBreakBlockPacket
import site.liangbai.realhomehunt.util.Blocks.sendClearBreakAnimationPacket
import site.liangbai.realhomehunt.util.Guns
import site.liangbai.realhomehunt.util.asLangText
import site.liangbai.realhomehunt.util.kt.isIgnoreGun
import site.liangbai.realhomehunt.util.kt.isIgnoreHitBlock
import taboolib.common.platform.function.submit
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

/**
 * The type Gun hit block processor.
 *
 * @author Liangbai
 * @since 2021 /08/11 02:48 下午
 */
class GunHitBlockProcessorImpl : IGunHitBlockProcessor {
    private val executor = Executors.newSingleThreadExecutor()

    override val damageCachePoolMap: MutableMap<UUID, DamageCachePool> = ConcurrentHashMap()

    override fun processGunHitBlock(player: Player, gun: ItemStack, block: Block) {
        if (!ResidenceManager.isOpened(player.world)) {
            return
        }
        if (gun.type.isIgnoreGun()) {
            return
        }
        if (block.type.isIgnoreHitBlock()) {
            return
        }
        val damageCachePool = damageCachePoolMap.computeIfAbsent(player.uniqueId) {
            DamageCachePool()
        }

        executor.execute {
            val residence = ResidenceManager.getResidenceByLocation(block.location)
            if (residence == null || residence.isAdministrator(player)) {
                return@execute
            }
            val damageCache = damageCachePool.getDamageCacheByBlock(player, residence, block,
                { Config.block.custom.getHardness(block) }
            ) {
                val title = player.asLangText("action-hit-block-performer-boss-bar", residence.owner, 0, 0)
                BossBarFactory.newHealthBossBar(title, 70, 30)
            }
            if (!AsyncResidenceHurtEvent(
                    player,
                    residence,
                    block,
                    gun,
                    damageCache,
                    damageCache.hardness
                ).post()
            ) {
                return@execute
            }
            if (damageCache.hardness <= 0) {
                damageCachePool.removeDamageCache(damageCache)
                return@execute
            }
            damageCache.increaseDamage(Guns.countDamage(gun))
            val healthBossBar = damageCache.healthBossBar
            if (Config.showBlockHealth && !Config.showOnlyTargetBlock) {
                healthBossBar.show(player)
            }
            if (damageCache.damage >= damageCache.hardness) {
                sendClearBreakAnimationPacket(damageCache.id, damageCache.block)
                healthBossBar.clearForHealth(damageCache)
                submit {
                    val blockData = damageCache.block.blockData.clone()
                    val callBack = GameModeManager.submit(residence, player, gun, block, block.state, blockData)
                    sendBreakBlockPacket(damageCache.block, Config.dropItem && callBack.get())
                }
                damageCachePool.removeDamageCache(damageCache)
            } else {
                val blockSit = Guns.countBlockSit(damageCache.damage, damageCache.hardness)
                healthBossBar.updateForHealth(damageCache)
                sendBreakAnimationPacket(damageCache.id, damageCache.block, blockSit)
                damageCache.delayUnload(Config.maxWaitMills)
            }
            if (!residence.hasAttack(player.name)) {
                residence.attackBy(player)
            }
        }
    }
}
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

package site.liangbai.realhomehunt

import com.craftingdead.core.event.GunEvent
import net.minecraftforge.fml.ModList
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.configuration.serialization.ConfigurationSerialization
import site.liangbai.dynamic.Dynamic
import site.liangbai.forgeeventbridge.event.EventBridge
import site.liangbai.realhomehunt.api.locale.manager.LocaleManager
import site.liangbai.realhomehunt.api.residence.Residence
import site.liangbai.realhomehunt.api.residence.attribute.map.AttributeMap
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager
import site.liangbai.realhomehunt.common.config.Config
import site.liangbai.realhomehunt.internal.listener.forge.block.EventHolderTryPierceableBlock
import site.liangbai.realhomehunt.internal.listener.forge.player.EventHolderGunHitBlock
import site.liangbai.realhomehunt.internal.task.PlayerGlowTask
import site.liangbai.realhomehunt.internal.task.PlayerMoveToResidenceMessageTask
import site.liangbai.realhomehunt.internal.task.ShowOnlyTargetBlockTask
import site.liangbai.realhomehunt.util.Console
import site.liangbai.realhomehuntforge.event.BlockRayTraceEvent
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.disablePlugin
import taboolib.common.platform.function.pluginId
import taboolib.common.platform.function.pluginVersion
import taboolib.platform.BukkitPlugin

object RealHomeHuntPlugin : Plugin() {
    val inst by lazy {
        BukkitPlugin.getInstance()
    }

    private const val FORGE_EVENT_BRIDGE_MOD_ID = "forgeeventbridge"
    private const val REAL_HOME_HUNT_FORGE_MOD_ID = "realhomehuntforge"

    override fun onEnable() {
        Config.init(inst)

        LocaleManager.init(inst)

        ResidenceManager.init(inst, Config.storage.type)

        Dynamic.installWithAccepted(inst, this::class.java.`package`.name)

        initTasks()

        processSuccess()
    }

    override fun onDisable() {
        saveResidences()
        doCloseStorage()
    }

    private fun doCloseStorage() {
        ResidenceManager.getStorage().close()
    }

    private fun saveResidences() {
        ResidenceManager.getResidences().forEach(Residence::save)
    }

    @Awake(LifeCycle.LOAD)
    private fun initForgeEventHolder() {
        checkForgeEventBridgeInst()

        EventHolderGunHitBlock().register(EventBridge.builder()
            .target(GunEvent.HitBlock::class.java).build())

        tryInitRealHomeHuntForge()
    }

    private fun checkForgeEventBridgeInst() {
        if (!ModList.get().isLoaded(FORGE_EVENT_BRIDGE_MOD_ID)) {
            disablePlugin()

            throw IllegalStateException("can not found Forge-Event-Bridge mod, please install it.")
        }
    }

    private fun tryInitRealHomeHuntForge() {
        if (ModList.get().isLoaded(REAL_HOME_HUNT_FORGE_MOD_ID)) {
            EventHolderTryPierceableBlock().register(EventBridge.builder()
                    .target(BlockRayTraceEvent.TryPierceableBlock::class.java).build())
        } else {
            Console.sendRawMessage("${ChatColor.YELLOW}WARN: ${ChatColor.RED}Not found RealHomeHuntForge, and some features will fail.")
        }
    }

    @Awake(LifeCycle.ENABLE)
    private fun initConfigurationSerializer() {
        ConfigurationSerialization.registerClass(Residence::class.java)
        ConfigurationSerialization.registerClass(Residence.IgnoreBlockInfo::class.java)

        AttributeMap.registerAttributeSerializer()
    }

    private fun initTasks() {
        PlayerMoveToResidenceMessageTask.setup(inst)
        PlayerGlowTask.setup(inst)
        ShowOnlyTargetBlockTask.setup(inst)
    }

    @Awake(LifeCycle.DISABLE)
    private fun cancelTasks() {
        Bukkit.getScheduler().cancelTasks(inst)
    }

    private fun processSuccess() {
        Console.sendRawMessage("${ChatColor.GREEN}Succeed in enabling $pluginId v$pluginVersion plugin, unique author: Liangbai.")
    }
}

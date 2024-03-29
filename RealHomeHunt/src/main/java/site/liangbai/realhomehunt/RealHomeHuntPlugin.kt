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

package site.liangbai.realhomehunt

import io.izzel.arclight.api.Arclight
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.ModList
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.configuration.serialization.ConfigurationSerialization
import site.liangbai.realhomehunt.api.nms.NMS
import site.liangbai.realhomehunt.api.residence.Residence
import site.liangbai.realhomehunt.api.residence.attribute.map.AttributeMap
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager
import site.liangbai.realhomehunt.common.config.Config
import site.liangbai.realhomehunt.internal.listener.forge.arclight.EventHandlerBlockDestroy
import site.liangbai.realhomehunt.internal.listener.forge.arclight.EventHandlerGunHitBlock
import site.liangbai.realhomehunt.internal.listener.forge.arclight.EventHandlerTryPierceableBlock
import site.liangbai.realhomehunt.internal.storage.impl.SqlStorage
import site.liangbai.realhomehunt.util.kt.isArclight
import site.liangbai.realhomehunt.util.pluginInfo
import taboolib.common.LifeCycle
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.Awake
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.disablePlugin
import taboolib.common.platform.function.info
import taboolib.common.platform.function.pluginId
import taboolib.common.platform.function.pluginVersion
import taboolib.module.lang.Language
import taboolib.module.nms.MinecraftVersion
import taboolib.platform.BukkitPlugin

/**
 * @author Liangbai
 */
@RuntimeDependency("!com.google.code.gson:gson:2.8.8", test = "!com.google.gson.Gson")
object RealHomeHuntPlugin : Plugin() {
    val inst by lazy {
        BukkitPlugin.getInstance()
    }

    private const val REAL_HOME_HUNT_FORGE_MOD_ID = "realhomehuntforge"

    override fun onLoad() {
        if (MinecraftVersion.majorLegacy < 11800 || !MinecraftVersion.isSupported) {
            info("The version ${MinecraftVersion.runningVersion} is not supported, at least be 1.18+")

            disablePlugin()
        }
    }

    @Awake(LifeCycle.ENABLE)
    fun init() {
        Config.init(inst)
        Language.reload()
        if (ResidenceManager.enabled) {
            closeStorage()
        }
        ResidenceManager.init(inst, Config.storage.type)
        processSuccess()
    }

    @Awake(LifeCycle.DISABLE)
    private fun closeStorage() {
        saveResidences()
        ResidenceManager.storage.close()
    }

    private fun saveResidences() {
        ResidenceManager.getResidences().forEach(Residence::save)
    }

    private fun processSuccess() {
        pluginInfo("${ChatColor.GREEN}Succeed in enabling $pluginId v$pluginVersion plugin, unique author: Liangbai.")
    }

    @Awake(LifeCycle.ENABLE)
    private fun initForgeEventListener() {
        if (isArclight()) {
            pluginInfo("${ChatColor.YELLOW}WARN: If the plugin throw the NullPointerException, please update your Arclight version to 1.0.3 or newer.")
            val bus = MinecraftForge.EVENT_BUS
            if (!ModList.get().isLoaded(REAL_HOME_HUNT_FORGE_MOD_ID)) {
                pluginInfo("${ChatColor.YELLOW}WARN: Could not found the RealHomeHuntForge Mod, it may produce error.")
            } else {
                Arclight.registerForgeEvent(inst, bus, EventHandlerTryPierceableBlock())
                Arclight.registerForgeEvent(inst, bus, EventHandlerBlockDestroy())
            }
            Arclight.registerForgeEvent(inst, bus, EventHandlerGunHitBlock())
        } else {
            disablePlugin()
            throw IllegalStateException("can not found Arclight server.")
        }
    }

    @Awake(LifeCycle.ACTIVE)
    private fun optimize() {
        inst
        NMS.INSTANCE
        val storage = ResidenceManager.storage
        if (storage is SqlStorage<*, *>) {
            storage.dataSource
        }
    }

    @Awake(LifeCycle.ENABLE)
    private fun initConfigurationSerializer() {
        ConfigurationSerialization.registerClass(Residence::class.java)
        ConfigurationSerialization.registerClass(Residence.IgnoreBlockCounter::class.java)

        AttributeMap.registerAttributeSerializer()
    }

    @Awake(LifeCycle.DISABLE)
    private fun cancelTasks() {
        Bukkit.getScheduler().cancelTasks(inst)
    }
}

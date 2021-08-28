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

package site.liangbai.realhomehunt.common.config

import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.block.Block
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import site.liangbai.realhomehunt.common.config.Config.BlockSetting.BlockCustomSetting
import site.liangbai.realhomehunt.common.config.Config.BlockSetting.BlockCustomSetting.CustomBlockInfo
import site.liangbai.realhomehunt.common.config.Config.BlockSetting.BlockIgnoreSetting
import site.liangbai.realhomehunt.common.config.Config.ResidenceSetting.ResidenceSizeSetting
import site.liangbai.realhomehunt.common.config.Config.ResidenceSetting.ResidenceToolSetting
import site.liangbai.realhomehunt.common.config.Config.ResidenceSetting.ResidenceToolSetting.ParticleColorSetting
import site.liangbai.realhomehunt.common.config.Config.RobChestModeSetting.DropItemSetting
import site.liangbai.realhomehunt.common.config.Config.StorageSetting.MySqlSetting
import site.liangbai.realhomehunt.common.config.Config.StorageSetting.SqliteSetting
import site.liangbai.realhomehunt.common.item.ItemType
import site.liangbai.realhomehunt.internal.color.ProxyColor
import site.liangbai.realhomehunt.internal.storage.StorageType
import site.liangbai.realhomehunt.util.Console
import site.liangbai.realhomehunt.util.kt.asTicks
import site.liangbai.realhomehunt.util.kt.colored
import java.io.File
import java.util.*

object Config {
    private lateinit var configFile: File

    lateinit var prefix: String

    lateinit var storage: StorageSetting

    lateinit var residence: ResidenceSetting

    lateinit var block: BlockSetting

    lateinit var robChestMode: RobChestModeSetting

    lateinit var autoFixResidence: AutoFixResidenceSetting

    lateinit var openWorlds: List<String>

    @JvmField
    var maxWaitMills: Long = 0
    @JvmField
    var teleportMills: Long = 0
    @JvmField
    var unloadPlayerAttackMills: Long = 0
    @JvmField
    var unloadWarnMills: Long = 0
    @JvmField
    var gunDamageMultiple = 0.0
    @JvmField
    var defaultBlockHardnessMultiple = 0.0
    @JvmField
    var perPowerLevelDamage = 0.0

    lateinit var consoleLanguage: String

    @JvmField
    var showActionBar = false

    @JvmField
    var showBlockHealth = false
    @JvmField
    var showOnlyTargetBlock = false
    @JvmField
    var actionBarShowMills: Long = 0
    @JvmField
    var confirmWaitMills: Long = 0
    @JvmField
    var dropItem = false
    fun init(plugin: Plugin) {
        val file = File(plugin.dataFolder, "config.yml")
        if (!file.exists()) {
            plugin.saveDefaultConfig()
        }
        configFile = file
        reload()
    }

    private fun reload() {
        Console.sendMessage(ChatColor.GREEN.toString() + "Reload config...")
        val yamlConfiguration = YamlConfiguration.loadConfiguration(
            configFile
        )
        prefix = yamlConfiguration.getString("prefix", "")!!.colored()
        openWorlds = yamlConfiguration.getStringList("openWorlds")
        maxWaitMills = yamlConfiguration.getLong("maxWaitMills", 5).asTicks()
        teleportMills = yamlConfiguration.getLong("teleportMills", 10).asTicks()
        unloadPlayerAttackMills = yamlConfiguration.getLong("unloadPlayerAttackMills", 600).asTicks()
        unloadWarnMills = yamlConfiguration.getLong("unloadWarnMills", 10).asTicks()
        gunDamageMultiple = yamlConfiguration.getDouble("gunDamageMultiple", 4.0)
        defaultBlockHardnessMultiple = yamlConfiguration.getDouble("defaultBlockHardnessMultiple", 3.2)
        perPowerLevelDamage = yamlConfiguration.getDouble("perPowerLevelDamage", 1.0)
        consoleLanguage = yamlConfiguration.getString("consoleLanguage", "zh_cn")!!
        showActionBar = yamlConfiguration.getBoolean("showActionBar", true)
        showBlockHealth = yamlConfiguration.getBoolean("showBlockHealth", true)
        showOnlyTargetBlock = yamlConfiguration.getBoolean("showOnlyTargetBlock", true)
        actionBarShowMills = yamlConfiguration.getLong("actionBarShowMills", 600).asTicks()
        confirmWaitMills = yamlConfiguration.getLong("confirmWaitMills", 15).asTicks()
        dropItem = yamlConfiguration.getBoolean("dropItem", false)
        Console.sendRawMessage(ChatColor.GREEN.toString() + "Linking storage settings...")
        linkStorageConfig(yamlConfiguration)
        Console.sendRawMessage(ChatColor.GREEN.toString() + "Linking storage settings successful.")
        Console.sendRawMessage(ChatColor.GREEN.toString() + "Linking residence settings...")
        linkResidenceConfig(yamlConfiguration)
        Console.sendRawMessage(ChatColor.GREEN.toString() + "Linking residence settings successful.")
        Console.sendRawMessage(ChatColor.GREEN.toString() + "Linking block settings...")
        linkBlockConfig(yamlConfiguration)
        Console.sendRawMessage(ChatColor.GREEN.toString() + "Linking block settings successful.")
        Console.sendRawMessage(ChatColor.GREEN.toString() + "Linking auto fix residence settings...")
        linkAutoFixResidenceConfig(yamlConfiguration)
        Console.sendRawMessage(ChatColor.GREEN.toString() + "Linking auto fix residence settings successful.")
        Console.sendRawMessage(ChatColor.GREEN.toString() + "Linking rob chest mode settings...")
        linkRobChestModeConfig(yamlConfiguration)
        Console.sendRawMessage(ChatColor.GREEN.toString() + "Linking rob chest mode settings successful.")
        Console.sendRawMessage(ChatColor.GREEN.toString() + "Reload config successful.")
    }

    private fun linkResidenceConfig(section: ConfigurationSection) {
        val residenceSection = section.getConfigurationSection("residence")
            ?: throw IllegalStateException("can not load config part: residence")
        residence = ResidenceSetting()
        residence.bannedCreateInResidence = residenceSection.getBoolean("banned-create-in-residence", true)
        val residenceSizeLimitSection = residenceSection.getConfigurationSection("sizeLimit")
            ?: throw IllegalStateException("can not load config part: residence.sizeLimit")
        val sizeSetting = ResidenceSizeSetting()
        sizeSetting.x = residenceSizeLimitSection.getInt("x", 16)
        sizeSetting.y = residenceSizeLimitSection.getInt("y", 16)
        sizeSetting.z = residenceSizeLimitSection.getInt("z", 16)
        residence.sizeLimit = sizeSetting
        val residenceToolSection = residenceSection.getConfigurationSection("tool")
            ?: throw IllegalStateException("can not load config part: residence.tool")
        val toolSetting = ResidenceToolSetting()
        val left = residenceToolSection.getString("leftSelect", "STICK")
        val right = residenceToolSection.getString("rightSelect", "STICK")
        val leftSelect = left?.let { Material.matchMaterial(it) } ?: Material.STICK
        val rightSelect = right?.let { Material.matchMaterial(it) } ?: Material.STICK
        toolSetting.leftSelect = leftSelect
        toolSetting.rightSelect = rightSelect
        toolSetting.showSelectZone = residenceToolSection.getBoolean("showSelectZone", true)
        val particleName = residenceToolSection.getString("showParticle", "REDSTONE")!!.uppercase()
        try {
            toolSetting.showParticle = Particle.valueOf(particleName)
        } catch (t: Throwable) {
            Console.sendRawMessage(ChatColor.RED.toString() + "can not find the particle: " + particleName + ", use default: REDSTONE")
            toolSetting.showParticle = Particle.REDSTONE
        }
        toolSetting.showParticleStep = residenceToolSection.getDouble("showParticleStep", 0.1)
        val particleColorSection = residenceToolSection.getConfigurationSection("particleColor")
            ?: throw IllegalStateException("can not load config part: residence.tool.particleColor")
        val particleColorSetting = ParticleColorSetting()
        particleColorSetting.enabled = particleColorSection.getBoolean("enabled", true)
        particleColorSetting.color = if (particleColorSection.getBoolean("useRGB", false)) Color.fromRGB(
            particleColorSection.getInt("red", 255),
            particleColorSection.getInt("green", 0),
            particleColorSection.getInt("blue", 0)
        ) else ProxyColor.matches(particleColorSection.getString("name", "RED"), ProxyColor.RED).toColor()
        toolSetting.particleColor = particleColorSetting
        residence.tool = toolSetting
        residence.openWarn = residenceSection.getBoolean("openWarn", true)
    }

    private fun linkBlockConfig(section: ConfigurationSection) {
        val blockSection = section.getConfigurationSection("block")
            ?: throw IllegalStateException("can not load config part: block")
        block = BlockSetting()
        block.bannedNotSolid = blockSection.getBoolean("bannedNotSolid", true)
        val blockIgnoreSection = blockSection.getConfigurationSection("ignore")
        val ignore = BlockIgnoreSetting()
        blockIgnoreSection?.getKeys(false)!!.filter {
            blockIgnoreSection.isConfigurationSection(it)
        }.mapNotNull {
            blockIgnoreSection.getConfigurationSection(it)
        }.forEach {
            val prefix = it.getString("prefix", "")!!.uppercase()
            val suffix =it.getString("suffix", "")!!.uppercase()
            val full = it.getString("full")
            val amount = it.getInt("amount", -1)
            val upBreak = it.getBoolean("upBreak", false)
            val ignoreHit = it.getBoolean("ignoreHit", false)
            val useCustomPierceable = it.contains("customPierceable")
            val customPierceable = it.getBoolean("customPierceable", true)
            val fixTime = it.getLong("fixTime", -1)
            val info = BlockIgnoreSetting.IgnoreBlockInfo()
            info.full = full
            info.prefix = prefix
            info.suffix = suffix
            info.amount = amount
            info.isUpBreak = upBreak
            info.ignoreHit = ignoreHit
            info.useCustomPierceable = useCustomPierceable
            info.customPierceable = customPierceable
            if (fixTime > -1) {
                info.fixTime = fixTime.asTicks()
            }
            ignore.ignoreBlockInfoList.add(info)
        }
        block.ignore = ignore
        val blockCustomSection = blockSection.getConfigurationSection("custom")
        val custom = BlockCustomSetting()
        blockCustomSection?.getKeys(false)!!.filter {
            blockCustomSection.isConfigurationSection(
                it
            )
        }.mapNotNull {
            blockCustomSection.getConfigurationSection(
                it
            )
        }.forEach {
            val type = it.getString("type") ?: throw IllegalStateException(
                "can not load config part: block.custom.$it.type"
            )
            val hardness = it.getDouble("hardness")
            val info = CustomBlockInfo()
            info.type = type
            info.hardness = hardness
            custom.customSettingList.add(info)
        }
        block.custom = custom
    }

    private fun linkStorageConfig(section: ConfigurationSection) {
        val storageSection = section.getConfigurationSection("storage")
            ?: throw IllegalStateException("can not load config part: storage")
        storage = StorageSetting()
        val type = storageSection.getString("type", "SQLITE")
        storage.type = StorageType.matchStorageType(type)
        val sqliteSection = storageSection.getConfigurationSection("sqlite")
            ?: throw IllegalStateException("can not load config part: storage.sqlite")
        val sqliteSetting = SqliteSetting()
        sqliteSetting.onlyInPluginFolder = sqliteSection.getBoolean("onlyInPluginFolder", true)
        val databaseFile = sqliteSection.getString("databaseFile", "residences.db")
            ?: throw IllegalStateException("can not load config part: storage.sqlite.databaseFile")
        sqliteSetting.databaseFile = databaseFile
        storage.sqliteSetting = sqliteSetting
        val mySqlSection = storageSection.getConfigurationSection("mysql")
            ?: throw IllegalStateException("can not load config part: storage.mysql")
        val mySqlSetting = MySqlSetting()
        val address = mySqlSection.getString("address", "localhost")
            ?: throw IllegalStateException("can not load config part: storage.mysql.address")
        val port = mySqlSection.getInt("port", 3306)
        val user = mySqlSection.getString("user", "root")
            ?: throw IllegalStateException("can not load config part: storage.mysql.user")
        val password = mySqlSection.getString("password", "123456")
            ?: throw IllegalStateException("can not load config part: storage.mysql.password")
        var options = mySqlSection.getString("options", "")
        if (options == null) options = ""
        mySqlSetting.address = address
        mySqlSetting.port = port
        mySqlSetting.user = user
        mySqlSetting.password = password
        mySqlSetting.options = options
        storage.mySqlSetting = mySqlSetting
    }

    private fun linkRobChestModeConfig(section: ConfigurationSection) {
        val modeSection = section.getConfigurationSection("robChestMode")
            ?: throw IllegalStateException("can not load config part: robChestMode")
        robChestMode = RobChestModeSetting()
        robChestMode.enabled = modeSection.getBoolean("enabled", false)
        robChestMode.fixItem = modeSection.getBoolean("fixItem", true)
        val dropItemSection = modeSection.getConfigurationSection("dropItem")
            ?: throw IllegalStateException("can not load config part: robChestMode.dropItem")
        robChestMode.dropItem = DropItemSetting()
        dropItemSection.getKeys(false)
            .mapNotNull {
                dropItemSection.getConfigurationSection(
                    it
                )
            }
            .filter { (it.contains("globalType") || it.contains("type")) && it.contains("chance") }
            .forEach {
                val globalType = it.getString("globalType")
                val chance = it.getDouble("chance")
                if (globalType != null) {
                    val itemType = ItemType.matchType(globalType)
                        ?: throw IllegalStateException("can not find the Item Type: " + globalType + " in the part: " + it.name)
                    robChestMode.dropItem.itemTypeToDoubleChanceEnumMap[itemType] = chance
                    return@forEach
                }
                val type = it.getString("type")
                val material = type?.let { it1 -> Material.matchMaterial(it1) }
                    ?: throw IllegalStateException("can not find the Item Type: " + type + " in the part: " + it.name)
                robChestMode.dropItem.materialToDoubleChanceEnumMap[material] = chance
            }
    }

    private fun linkAutoFixResidenceConfig(section: ConfigurationSection) {
        val autoFixResidenceSection = section.getConfigurationSection("autoFixResidence")
            ?: throw IllegalStateException("can not load config part: autoFixResidence")
        autoFixResidence = AutoFixResidenceSetting()
        autoFixResidence.enabled = autoFixResidenceSection.getBoolean("enabled", true)
        autoFixResidence.perBlockFixedMills = autoFixResidenceSection.getLong("perBlockFixedMills", 15).asTicks()
        autoFixResidence.ignoreEnemy = autoFixResidenceSection.getBoolean("ignoreEnemy", false)
        autoFixResidence.ignoreBlockTypes.addAll(autoFixResidenceSection.getStringList("ignoreBlockTypes")
            .mapNotNull {
                Material.matchMaterial(
                    it
                )
            })
    }

    class StorageSetting {
        lateinit var type: StorageType

        lateinit var sqliteSetting: SqliteSetting

        lateinit var mySqlSetting: MySqlSetting

        class SqliteSetting {
            @JvmField
            var onlyInPluginFolder = false

            lateinit var databaseFile: String
        }

        class MySqlSetting {

            lateinit var address: String

            @JvmField
            var port = 0

            lateinit var user: String

            lateinit var password: String

            lateinit var options: String
        }
    }

    class ResidenceSetting {
        var bannedCreateInResidence = false

        lateinit var sizeLimit: ResidenceSizeSetting

        lateinit var tool: ResidenceToolSetting
        @JvmField
        var openWarn = false

        class ResidenceSizeSetting {
            var x = 0
            var y = 0
            var z = 0
        }

        class ResidenceToolSetting {

            lateinit var leftSelect: Material

            lateinit var rightSelect: Material
            @JvmField
            var showSelectZone = false

            lateinit var showParticle: Particle
            @JvmField
            var showParticleStep = 0.0

            lateinit var particleColor: ParticleColorSetting

            class ParticleColorSetting {
                @JvmField
                var enabled = false

                lateinit var color: Color
            }
        }
    }

    class BlockSetting {
        var bannedNotSolid = false

        lateinit var ignore: BlockIgnoreSetting

        lateinit var custom: BlockCustomSetting

        class BlockIgnoreSetting {
            val ignoreBlockInfoList: MutableList<IgnoreBlockInfo> = mutableListOf()

            class IgnoreBlockInfo {
                lateinit var prefix: String
                lateinit var suffix: String
                @JvmField
                var full: String? = null
                @JvmField
                var amount = 0
                var isUpBreak = false
                var ignoreHit = false
                var useCustomPierceable = false
                var customPierceable = false
                var fixTime: Long = -1
                var temp = false

                constructor(
                    prefix: String,
                    suffix: String,
                    full: String?,
                    amount: Int,
                    upBreak: Boolean,
                    temp: Boolean
                ) {
                    this.prefix = prefix
                    this.suffix = suffix
                    this.full = full
                    this.amount = amount
                    isUpBreak = upBreak
                    this.temp = temp
                }

                constructor()
            }

            fun getFixedTime(material: Material): Long {
                val info = getByMaterial(material)
                return if (info == null || info.fixTime < 0) {
                    autoFixResidence.perBlockFixedMills
                } else info.fixTime
            }

            fun isPierceable(material: Material, original: Boolean): Boolean {
                val info = getByMaterial(material)

                // 方便理解
                return if (info == null || info.temp || !info.useCustomPierceable) {
                    original
                } else info.customPierceable
            }

            fun isIgnoreHit(material: Material): Boolean {
                val info = getByMaterial(material)
                return info != null && info.ignoreHit
            }

            fun getByMaterial(material: Material): IgnoreBlockInfo? {
                val name = material.name.uppercase()
                return ignoreBlockInfoList.firstOrNull {
                    (it.full != null && it.full.equals(
                        name,
                        ignoreCase = true
                    )) || ((it.prefix.isNotEmpty() || it.suffix.isNotEmpty()) && name.startsWith(
                        it.prefix
                    ) && name.endsWith(it.suffix))
                }.let {
                    it ?: if (!material.isSolid && block.bannedNotSolid) IgnoreBlockInfo(
                            "",
                            "",
                            material.name,
                            0,
                            upBreak = false,
                            temp = true
                        ) else null
                }
            }
        }

        class BlockCustomSetting {
            val customSettingList: MutableList<CustomBlockInfo> = mutableListOf()

            class CustomBlockInfo {
                lateinit var type: String
                var hardness = 0.0
            }

            fun getHardness(block: Block): Double {
                return customSettingList
                    .filter {
                        it.type.equals(
                            block.type.name,
                            ignoreCase = true
                        )
                    }
                    .map { it.hardness }
                    .firstOrNull()
                    .let { it ?: (block.type.hardness * defaultBlockHardnessMultiple) }
            }
        }
    }

    class RobChestModeSetting {
        @JvmField
        var enabled = false
        var fixItem = false

        lateinit var dropItem: DropItemSetting

        class DropItemSetting {
            @JvmField
            val itemTypeToDoubleChanceEnumMap: MutableMap<ItemType, Double> = EnumMap(ItemType::class.java)
            val materialToDoubleChanceEnumMap: MutableMap<Material, Double> = EnumMap(Material::class.java)
            fun getChance(itemStack: ItemStack): Double {
                val type = itemStack.type
                return if (materialToDoubleChanceEnumMap.containsKey(type)) {
                    materialToDoubleChanceEnumMap[type]!!
                } else ItemType.matches(itemStack).chance
            }
        }
    }

    class AutoFixResidenceSetting {
        @JvmField
        var enabled = false
        var perBlockFixedMills: Long = 0
        @JvmField
        var ignoreEnemy = false
        @JvmField
        val ignoreBlockTypes: MutableList<Material?> = mutableListOf()
    }
}
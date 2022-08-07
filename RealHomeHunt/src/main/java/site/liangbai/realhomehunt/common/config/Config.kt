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
import site.liangbai.realhomehunt.util.pluginInfo
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

    lateinit var gun: GunSetting

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
        openWorlds = yamlConfiguration.getStringList("open-worlds")
        maxWaitMills = yamlConfiguration.getLong("max-wait-mills", 5).asTicks()
        teleportMills = yamlConfiguration.getLong("teleport-mills", 10).asTicks()
        unloadPlayerAttackMills = yamlConfiguration.getLong("unload-player-attack-mills", 600).asTicks()
        unloadWarnMills = yamlConfiguration.getLong("unload-warn-mills", 10).asTicks()
        gunDamageMultiple = yamlConfiguration.getDouble("gun-damage-multiple", 4.0)
        defaultBlockHardnessMultiple = yamlConfiguration.getDouble("default-block-hardness-multiple", 3.2)
        perPowerLevelDamage = yamlConfiguration.getDouble("per-power-level-damage", 1.0)
        showActionBar = yamlConfiguration.getBoolean("show-action-bar", true)
        showBlockHealth = yamlConfiguration.getBoolean("show-block-health", true)
        showOnlyTargetBlock = yamlConfiguration.getBoolean("show-only-target-block", true)
        actionBarShowMills = yamlConfiguration.getLong("action-bar-show-mills", 600).asTicks()
        confirmWaitMills = yamlConfiguration.getLong("confirm-wait-mills", 15).asTicks()
        dropItem = yamlConfiguration.getBoolean("drop-item", false)
        pluginInfo(ChatColor.GREEN.toString() + "Linking storage settings...")
        linkStorageConfig(yamlConfiguration)
        pluginInfo(ChatColor.GREEN.toString() + "Linking storage settings successful.")
        pluginInfo(ChatColor.GREEN.toString() + "Linking residence settings...")
        linkResidenceConfig(yamlConfiguration)
        pluginInfo(ChatColor.GREEN.toString() + "Linking residence settings successful.")
        pluginInfo(ChatColor.GREEN.toString() + "Linking block settings...")
        linkBlockConfig(yamlConfiguration)
        pluginInfo(ChatColor.GREEN.toString() + "Linking block settings successful.")
        pluginInfo(ChatColor.GREEN.toString() + "Linking auto fix residence settings...")
        linkAutoFixResidenceConfig(yamlConfiguration)
        pluginInfo(ChatColor.GREEN.toString() + "Linking auto fix residence settings successful.")
        pluginInfo(ChatColor.GREEN.toString() + "Linking rob chest mode settings...")
        linkRobChestModeConfig(yamlConfiguration)
        pluginInfo(ChatColor.GREEN.toString() + "Linking rob chest mode settings successful.")
        pluginInfo(ChatColor.GREEN.toString() + "Linking gun settings...")
        linkGunConfig(yamlConfiguration)
        pluginInfo(ChatColor.GREEN.toString() + "Linking gun settings successful.")
        pluginInfo(ChatColor.GREEN.toString() + "Reload config successful.")
    }

    private fun linkResidenceConfig(section: ConfigurationSection) {
        val residenceSection = section.getConfigurationSection("residence")
            ?: throw IllegalStateException("can not load config part: residence")
        residence = ResidenceSetting()
        residence.bannedCreateInResidence = residenceSection.getBoolean("banned-create-in-residence", true)
        val residenceSizeLimitSection = residenceSection.getConfigurationSection("size-limit")
            ?: throw IllegalStateException("can not load config part: residence.size-limit")
        val sizeSetting = ResidenceSizeSetting()
        sizeSetting.x = residenceSizeLimitSection.getInt("x", 16)
        sizeSetting.y = residenceSizeLimitSection.getInt("y", 16)
        sizeSetting.z = residenceSizeLimitSection.getInt("z", 16)
        residence.sizeLimit = sizeSetting
        val residenceToolSection = residenceSection.getConfigurationSection("tool")
            ?: throw IllegalStateException("can not load config part: residence.tool")
        val toolSetting = ResidenceToolSetting()
        val left = residenceToolSection.getString("left-select", "STICK")
        val right = residenceToolSection.getString("right-select", "STICK")
        val leftSelect = left?.let { Material.matchMaterial(it) } ?: Material.STICK
        val rightSelect = right?.let { Material.matchMaterial(it) } ?: Material.STICK
        toolSetting.leftSelect = leftSelect
        toolSetting.rightSelect = rightSelect
        toolSetting.showSelectZone = residenceToolSection.getBoolean("show-select-zone", true)
        val particleName = residenceToolSection.getString("show-particle", "REDSTONE")!!.uppercase()
        try {
            toolSetting.showParticle = Particle.valueOf(particleName)
        } catch (t: Throwable) {
            pluginInfo(ChatColor.RED.toString() + "can not find the particle: " + particleName + ", use default: REDSTONE")
            toolSetting.showParticle = Particle.REDSTONE
        }
        toolSetting.showParticleStep = residenceToolSection.getDouble("show-particle-step", 0.1)
        val particleColorSection = residenceToolSection.getConfigurationSection("particle-color")
            ?: throw IllegalStateException("can not load config part: residence.tool.particle-color")
        val particleColorSetting = ParticleColorSetting()
        particleColorSetting.enabled = particleColorSection.getBoolean("enabled", true)
        particleColorSetting.color = if (particleColorSection.getBoolean("use-rgb", false)) Color.fromRGB(
            particleColorSection.getInt("red", 255),
            particleColorSection.getInt("green", 0),
            particleColorSection.getInt("blue", 0)
        ) else ProxyColor.matches(particleColorSection.getString("name", "RED"), ProxyColor.RED).toColor()
        toolSetting.particleColor = particleColorSetting
        residence.tool = toolSetting
        residence.openWarn = residenceSection.getBoolean("open-warn", true)
    }

    private fun linkBlockConfig(section: ConfigurationSection) {
        val blockSection = section.getConfigurationSection("block")
            ?: throw IllegalStateException("can not load config part: block")
        block = BlockSetting()
        block.bannedNotSolid = blockSection.getBoolean("banned-not-solid", true)
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
            if (prefix.isEmpty() && suffix.isEmpty() && full != null && full.isEmpty()) {
                throw IllegalStateException("can not load empty ignore block config body from: block.ignore.${it.name}")
            }
            val amount = it.getInt("amount", -1)
            val ignoreHit = it.getBoolean("ignore-hit", false)
            val useCustomPierceable = it.contains("custom-pierceable")
            val customPierceable = it.getBoolean("custom-pierceable", true)
            val fixTime = it.getLong("fix-time", -1)
            val info = BlockIgnoreSetting.IgnoreBlockInfo()
            info.full = full
            info.prefix = prefix
            info.suffix = suffix
            info.amount = amount
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

    private fun linkGunConfig(section: ConfigurationSection) {
        val gunSection = section.getConfigurationSection("gun")
            ?: throw IllegalStateException("can not load config part: gun")

        gun = GunSetting()
        val ignoreSetting = GunSetting.GunIgnoreSetting()
        ignoreSetting.ignoreList.addAll(
            gunSection.getStringList("ignore").mapNotNull { Material.matchMaterial(it) }
        )
        gun.ignore = ignoreSetting
        val customSection = gunSection.getConfigurationSection("custom")
        val customSetting = GunSetting.GunCustomSetting()
        customSetting.customInfoList.addAll(
            customSection?.getKeys(false)?.mapNotNull { customSection.getConfigurationSection(it) }?.map {
                val type = Material.matchMaterial(
                    it.getString("type") ?: throw throw IllegalStateException("can not load config part: gun.custom.${it.name}.type")
                ) ?: throw throw IllegalStateException("can not found the gun type: ${it.getString("type")}")

                val info = GunSetting.GunCustomSetting.GunCustomInfo(type, it.getDouble("damage", -1.0), it.getBoolean("without", false))

                info
            } ?: emptyList()
        )
        gun.custom = customSetting
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
        sqliteSetting.onlyInPluginFolder = sqliteSection.getBoolean("only-in-plugin-folder", true)
        val databaseFile = sqliteSection.getString("database-file", "residences.db")
            ?: throw IllegalStateException("can not load config part: storage.sqlite.database-file")
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
        val database = mySqlSection.getString("database", "")!!
        var options = mySqlSection.getString("options", "")
        if (options == null) options = ""
        mySqlSetting.address = address
        mySqlSetting.port = port
        mySqlSetting.user = user
        mySqlSetting.password = password
        mySqlSetting.database = database
        mySqlSetting.options = options
        storage.mySqlSetting = mySqlSetting
    }

    private fun linkRobChestModeConfig(section: ConfigurationSection) {
        val modeSection = section.getConfigurationSection("rob-chest-mode")
            ?: throw IllegalStateException("can not load config part: rob-chest-mode")
        robChestMode = RobChestModeSetting()
        robChestMode.enabled = modeSection.getBoolean("enabled", false)
        robChestMode.fixItem = modeSection.getBoolean("fix-item", true)
        val dropItemSection = modeSection.getConfigurationSection("drop-item")
            ?: throw IllegalStateException("can not load config part: rob-chest-mode.drop-item")
        robChestMode.dropItem = DropItemSetting()
        dropItemSection.getKeys(false)
            .mapNotNull {
                dropItemSection.getConfigurationSection(
                    it
                )
            }
            .filter { (it.contains("global-type") || it.contains("type")) && it.contains("chance") }
            .forEach {
                val globalType = it.getString("global-type")
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
        val autoFixResidenceSection = section.getConfigurationSection("auto-fix-residence")
            ?: throw IllegalStateException("can not load config part: auto-fix-residence")
        autoFixResidence = AutoFixResidenceSetting()
        autoFixResidence.enabled = autoFixResidenceSection.getBoolean("enabled", true)
        autoFixResidence.perBlockFixedMills = autoFixResidenceSection.getLong("per-block-fixed-mills", 15).asTicks()
        autoFixResidence.ignoreEnemy = autoFixResidenceSection.getBoolean("ignore-enemy", false)
        autoFixResidence.ignoreBlockTypes.addAll(autoFixResidenceSection.getStringList("ignore-block-types")
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

            lateinit var database: String

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
                    temp: Boolean
                ) {
                    this.prefix = prefix
                    this.suffix = suffix
                    this.full = full
                    this.amount = amount
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

    class GunSetting {
        lateinit var ignore: GunIgnoreSetting
        lateinit var custom: GunCustomSetting

        class GunIgnoreSetting {
            val ignoreList = mutableListOf<Material>()

            fun isIgnored(material: Material) = material in ignoreList
        }

        class GunCustomSetting {
            val customInfoList = mutableListOf<GunCustomInfo>()

            fun getCustomDamage(material: Material, original: Double, powerLevel: Int = 0): Double {
                return customInfoList.firstOrNull { it.type == material }?.let {
                    if (it.damage < 0) {
                        if (it.without) {
                            original
                        } else countDamage(it.damage, powerLevel)
                    } else if (it.without) it.damage else countDamage(it.damage, powerLevel)
                } ?: countDamage(original, powerLevel)
            }

            private fun countDamage(damage: Double, powerLevel: Int = 0): Double {
                return (damage + perPowerLevelDamage * powerLevel) / gunDamageMultiple
            }

            class GunCustomInfo(
                val type: Material,
                var damage: Double = -1.0,
                var without: Boolean = false
            )
        }
    }

    class RobChestModeSetting {
        @JvmField
        var enabled = false
        var fixItem = false

        lateinit var dropItem: DropItemSetting

        class DropItemSetting {
            @JvmField
            val itemTypeToDoubleChanceEnumMap: MutableMap<ItemType, Double> = EnumMap(
                ItemType::class.java)
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
        val ignoreBlockTypes: MutableList<Material> = mutableListOf()
    }
}
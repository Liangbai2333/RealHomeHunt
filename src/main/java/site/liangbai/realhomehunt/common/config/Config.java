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

package site.liangbai.realhomehunt.common.config;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import site.liangbai.realhomehunt.common.item.ItemType;
import site.liangbai.realhomehunt.internal.color.ProxyColor;
import site.liangbai.realhomehunt.util.Console;
import site.liangbai.realhomehunt.internal.storage.StorageType;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public final class Config {
    private static File configFile;

    public static String prefix;

    public static StorageSetting storage;

    public static ResidenceSetting residence;

    public static BlockSetting block;

    public static RobChestModeSetting robChestMode;

    public static AutoFixResidenceSetting autoFixResidence;

    public static List<String> openWorlds;

    public static long maxWaitMills;

    public static long teleportMills;

    public static long unloadPlayerAttackMills;

    public static long unloadWarnMills;

    public static double gunDamageMultiple;

    public static double defaultBlockHardnessMultiple;

    public static double perPowerLevelDamage;

    public static String consoleLanguage;

    public static boolean showActionBar;

    public static boolean showBlockHealth;

    public static long actionBarShowMills;

    public static long confirmWaitMills;

    public static boolean dropItem;

    public static void init(Plugin plugin) {
        File file = new File(plugin.getDataFolder(), "config.yml");

        if (!file.exists()) {
            plugin.saveDefaultConfig();
        }

        configFile = file;

        reload();
    }

    public static void reload() {
        Console.sendMessage(ChatColor.GREEN + "Reload config...");

        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(configFile);

        prefix = asColored(yamlConfiguration.getString("prefix", ""));

        openWorlds = yamlConfiguration.getStringList("openWorlds");

        maxWaitMills = asMills(yamlConfiguration.getLong("maxWaitMills", 5));

        teleportMills = asMills(yamlConfiguration.getLong("teleportMills", 10));

        unloadPlayerAttackMills = asMills(yamlConfiguration.getLong("unloadPlayerAttackMills", 600));

        unloadWarnMills = asMills(yamlConfiguration.getLong("unloadWarnMills", 10));

        gunDamageMultiple = yamlConfiguration.getDouble("gunDamageMultiple", 4);

        defaultBlockHardnessMultiple = yamlConfiguration.getDouble("defaultBlockHardnessMultiple", 3.2);

        perPowerLevelDamage = yamlConfiguration.getDouble("perPowerLevelDamage", 1);

        consoleLanguage = yamlConfiguration.getString("consoleLanguage", "zh_cn");

        showActionBar = yamlConfiguration.getBoolean("showActionBar", true);

        showBlockHealth = yamlConfiguration.getBoolean("showBlockHealth", true);

        actionBarShowMills = asMills(yamlConfiguration.getLong("actionBarShowMills", 600));

        confirmWaitMills = asMills(yamlConfiguration.getLong("confirmWaitMills", 15));

        dropItem = yamlConfiguration.getBoolean("dropItem", false);

        Console.sendRawMessage(ChatColor.GREEN + "Linking storage settings...");

        linkStorageConfig(yamlConfiguration);

        Console.sendRawMessage(ChatColor.GREEN + "Linking storage settings successful.");

        Console.sendRawMessage(ChatColor.GREEN + "Linking residence settings...");

        linkResidenceConfig(yamlConfiguration);

        Console.sendRawMessage(ChatColor.GREEN + "Linking residence settings successful.");

        Console.sendRawMessage(ChatColor.GREEN + "Linking block settings...");

        linkBlockConfig(yamlConfiguration);

        Console.sendRawMessage(ChatColor.GREEN + "Linking block settings successful.");

        Console.sendRawMessage(ChatColor.GREEN + "Linking auto fix residence settings...");

        linkAutoFixResidenceConfig(yamlConfiguration);

        Console.sendRawMessage(ChatColor.GREEN + "Linking auto fix residence settings successful.");

        Console.sendRawMessage(ChatColor.GREEN + "Linking rob chest mode settings...");

        linkRobChestModeConfig(yamlConfiguration);

        Console.sendRawMessage(ChatColor.GREEN + "Linking rob chest mode settings successful.");

        Console.sendRawMessage(ChatColor.GREEN + "Reload config successful.");
    }

    private static void linkResidenceConfig(ConfigurationSection section) {
        ConfigurationSection residenceSection = section.getConfigurationSection("residence");

        if (residenceSection == null) throw new IllegalStateException("can not load config part: residence");

        residence = new ResidenceSetting();

        ConfigurationSection residenceSizeLimitSection = residenceSection.getConfigurationSection("sizeLimit");

        if (residenceSizeLimitSection == null) throw new IllegalStateException("can not load config part: residence.sizeLimit");

        ResidenceSetting.ResidenceSizeSetting sizeSetting = new ResidenceSetting.ResidenceSizeSetting();

        sizeSetting.x = residenceSizeLimitSection.getInt("x", 16);
        sizeSetting.y = residenceSizeLimitSection.getInt("y", 16);
        sizeSetting.z = residenceSizeLimitSection.getInt("z", 16);

        residence.sizeLimit = sizeSetting;

        ConfigurationSection residenceToolSection = residenceSection.getConfigurationSection("tool");

        if (residenceToolSection == null) throw new IllegalStateException("can not load config part: residence.tool");

        ResidenceSetting.ResidenceToolSetting toolSetting = new ResidenceSetting.ResidenceToolSetting();

        String left = residenceToolSection.getString("leftSelect", "STICK");

        String right = residenceToolSection.getString("rightSelect", "STICK");

        Material leftSelect = Material.matchMaterial(Objects.requireNonNull(left));

        Material rightSelect = Material.matchMaterial(Objects.requireNonNull(right));

        toolSetting.leftSelect = leftSelect != null ? leftSelect : Material.STICK;

        toolSetting.rightSelect = rightSelect != null ? rightSelect : Material.STICK;

        toolSetting.showSelectZone = residenceToolSection.getBoolean("showSelectZone", true);

        String particleName = Objects.requireNonNull(residenceToolSection.getString("showParticle", "REDSTONE")).toUpperCase();
        try {
            toolSetting.showParticle = Particle.valueOf(particleName);
        } catch (Throwable t) {
            Console.sendRawMessage(ChatColor.RED + "can not find the particle: " + particleName + ", use default: REDSTONE");
            toolSetting.showParticle = Particle.REDSTONE;
        }

        toolSetting.showParticleStep = residenceToolSection.getDouble("showParticleStep", 0.1);

        ConfigurationSection particleColorSection = residenceToolSection.getConfigurationSection("particleColor");

        if (particleColorSection == null) throw new IllegalStateException("can not load config part: residence.tool.particleColor");

        ResidenceSetting.ResidenceToolSetting.ParticleColorSetting particleColorSetting = new ResidenceSetting.ResidenceToolSetting.ParticleColorSetting();

        particleColorSetting.enabled = particleColorSection.getBoolean("enabled", true);

        particleColorSetting.color = particleColorSection.getBoolean("useRGB", false) ?
                Color.fromRGB(particleColorSection.getInt("red", 255), particleColorSection.getInt("green", 0), particleColorSection.getInt("blue", 0)) :
                ProxyColor.matches(particleColorSection.getString("name", "RED"), ProxyColor.RED).toColor();

        toolSetting.particleColor = particleColorSetting;

        residence.tool = toolSetting;

        residence.openWarn = residenceSection.getBoolean("openWarn", true);
    }

    private static void linkBlockConfig(ConfigurationSection section) {
        ConfigurationSection blockSection = section.getConfigurationSection("block");

        if (blockSection == null) throw new IllegalStateException("can not load config part: block");

        block = new BlockSetting();

        block.bannedNotSolid = blockSection.getBoolean("bannedNotSolid", true);

        ConfigurationSection blockIgnoreSection = blockSection.getConfigurationSection("ignore");

        BlockSetting.BlockIgnoreSetting ignore = new BlockSetting.BlockIgnoreSetting();

        if (blockIgnoreSection != null) {
            blockIgnoreSection.getKeys(false).stream()
                    .filter(blockIgnoreSection::isConfigurationSection)
                    .map(blockIgnoreSection::getConfigurationSection)
                    .filter(Objects::nonNull)
                    .forEach(it -> {
                        String prefix = Objects.requireNonNull(it.getString("prefix", "")).toUpperCase();

                        String suffix = Objects.requireNonNull(it.getString("suffix", "")).toUpperCase();

                        String full = it.getString("full");

                        int amount = it.getInt("amount", -1);

                        boolean upBreak = it.getBoolean("upBreak", false);

                        boolean ignoreHit = it.getBoolean("ignoreHit", false);

                        boolean customPierceable = it.getBoolean("customPierceable", true);

                        BlockSetting.BlockIgnoreSetting.IgnoreBlockInfo info
                                = new BlockSetting.BlockIgnoreSetting.IgnoreBlockInfo();

                        info.full = full;

                        info.prefix = prefix;

                        info.suffix = suffix;

                        info.amount = amount;

                        info.upBreak = upBreak;

                        info.ignoreHit = ignoreHit;

                        ignore.ignoreBlockInfoList.add(info);
                    });
        }

        block.ignore = ignore;

        ConfigurationSection blockCustomSection = blockSection.getConfigurationSection("custom");

        BlockSetting.BlockCustomSetting custom = new BlockSetting.BlockCustomSetting();

        if (blockCustomSection != null) {
            blockCustomSection.getKeys(false).stream()
                    .filter(blockCustomSection::isConfigurationSection)
                    .map(blockCustomSection::getConfigurationSection)
                    .filter(Objects::nonNull)
                    .forEach(it -> {
                        String type = it.getString("type");

                        if (type == null) throw new IllegalStateException("can not load config part: block.custom." + it + ".type");

                        double hardness = it.getDouble("hardness");

                        BlockSetting.BlockCustomSetting.CustomBlockInfo info = new BlockSetting.BlockCustomSetting.CustomBlockInfo();

                        info.type = type;

                        info.hardness = hardness;

                        custom.customSettingList.add(info);
                    });
        }

        block.custom = custom;
    }

    private static void linkStorageConfig(ConfigurationSection section) {
        ConfigurationSection storageSection = section.getConfigurationSection("storage");

        if (storageSection == null) throw new IllegalStateException("can not load config part: storage");

        storage = new StorageSetting();

        String type = storageSection.getString("type", "SQLITE");

        storage.type = StorageType.matchStorageType(type);

        ConfigurationSection sqliteSection = storageSection.getConfigurationSection("sqlite");

        if (sqliteSection == null) throw new IllegalStateException("can not load config part: storage.sqlite");

        StorageSetting.SqliteSetting sqliteSetting = new StorageSetting.SqliteSetting();

        sqliteSetting.onlyInPluginFolder = sqliteSection.getBoolean("onlyInPluginFolder", true);

        String databaseFile = sqliteSection.getString("databaseFile", "residences.db");

        if (databaseFile == null) throw new IllegalStateException("can not load config part: storage.sqlite.databaseFile");

        sqliteSetting.databaseFile = databaseFile;

        storage.sqliteSetting = sqliteSetting;

        ConfigurationSection mySqlSection = storageSection.getConfigurationSection("mysql");

        if (mySqlSection == null) throw new IllegalStateException("can not load config part: storage.mysql");

        StorageSetting.MySqlSetting mySqlSetting = new StorageSetting.MySqlSetting();

        String address = mySqlSection.getString("address", "localhost");

        if (address == null) throw new IllegalStateException("can not load config part: storage.mysql.address");

        int port = mySqlSection.getInt("port", 3306);

        String user = mySqlSection.getString("user", "root");

        if (user == null) throw new IllegalStateException("can not load config part: storage.mysql.user");

        String password = mySqlSection.getString("password", "123456");

        if (password == null) throw new IllegalStateException("can not load config part: storage.mysql.password");

        String options = mySqlSection.getString("options", "");

        if (options == null) options = "";

        mySqlSetting.address = address;
        mySqlSetting.port = port;
        mySqlSetting.user = user;
        mySqlSetting.password = password;
        mySqlSetting.options = options;

        storage.mySqlSetting = mySqlSetting;
    }

    private static void linkRobChestModeConfig(ConfigurationSection section) {
        ConfigurationSection modeSection = section.getConfigurationSection("robChestMode");

        if (modeSection == null) throw new IllegalStateException("can not load config part: robChestMode");

        robChestMode = new RobChestModeSetting();

        robChestMode.enabled = modeSection.getBoolean("enabled", false);

        robChestMode.fixItem = modeSection.getBoolean("fixItem", true);

        ConfigurationSection dropItemSection = modeSection.getConfigurationSection("dropItem");

        if (dropItemSection == null) throw new IllegalStateException("can not load config part: robChestMode.dropItem");

        robChestMode.dropItem = new RobChestModeSetting.DropItemSetting();

        dropItemSection.getKeys(false).stream()
                .map(dropItemSection::getConfigurationSection)
                .filter(Objects::nonNull)
                .filter(it -> (it.contains("globalType") || it.contains("type")) && it.contains("chance"))
                .forEach(it -> {
                    String globalType = it.getString("globalType");

                    double chance = it.getDouble("chance");

                    if (globalType != null) {
                        ItemType itemType = ItemType.matchType(globalType);

                        if (itemType == null) throw new IllegalStateException("can not find the Item Type: " + globalType + " in the part: " + it.getName());

                        robChestMode.dropItem.itemTypeToDoubleChanceEnumMap.put(itemType, chance);

                        return;
                    }

                    String type = it.getString("type");

                    Material material = Material.matchMaterial(Objects.requireNonNull(type));

                    if (material == null) throw new IllegalStateException("can not find the Item Type: " + type + " in the part: " + it.getName());

                    robChestMode.dropItem.materialToDoubleChanceEnumMap.put(material, chance);
                });
    }

    private static void linkAutoFixResidenceConfig(ConfigurationSection section) {
        ConfigurationSection autoFixResidenceSection = section.getConfigurationSection("autoFixResidence");

        if (autoFixResidenceSection == null) throw new IllegalStateException("can not load config part: autoFixResidence");

        autoFixResidence = new AutoFixResidenceSetting();

        autoFixResidence.enabled = autoFixResidenceSection.getBoolean("enabled", true);

        autoFixResidence.perBlockFixedMills = asMills(autoFixResidenceSection.getLong("perBlockFixedMills", 15));

        autoFixResidence.ignoreEnemy = autoFixResidenceSection.getBoolean("ignoreEnemy", false);

        autoFixResidence.ignoreBlockTypes.addAll(autoFixResidenceSection.getStringList("ignoreBlockTypes").stream()
                .map(Material::matchMaterial)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
    }

    public static String asColored(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static long asMills(long seconds) {
        return seconds * 20;
    }

    public static final class StorageSetting {
        public StorageType type;

        public SqliteSetting sqliteSetting;

        public MySqlSetting mySqlSetting;

        public static final class SqliteSetting {
            public boolean onlyInPluginFolder;

            public String databaseFile;
        }

        public static final class MySqlSetting {
            public String address;

            public int port;

            public String user;

            public String password;

            public String options;
        }
    }

    public static final class ResidenceSetting {
        public ResidenceSizeSetting sizeLimit;

        public ResidenceToolSetting tool;

        public boolean openWarn;

        public static final class ResidenceSizeSetting {
            public int x;

            public int y;

            public int z;
        }

        public static final class ResidenceToolSetting {
            public Material leftSelect;

            public Material rightSelect;

            public boolean showSelectZone;

            public Particle showParticle;

            public double showParticleStep;

            public ParticleColorSetting particleColor;

            public static final class ParticleColorSetting {
                public boolean enabled;

                public Color color;
            }
        }
    }

    public static final class BlockSetting {
        public boolean bannedNotSolid;

        public BlockIgnoreSetting ignore;

        public BlockCustomSetting custom;

        public static final class BlockIgnoreSetting {
            public final List<IgnoreBlockInfo> ignoreBlockInfoList = new LinkedList<>();

            public static final class IgnoreBlockInfo {
                public String prefix;

                public String suffix;

                public String full;

                public int amount;

                public boolean upBreak;

                public boolean ignoreHit;

                public boolean customPierceable;

                public boolean isUpBreak() {
                    return upBreak;
                }

                public IgnoreBlockInfo(String prefix, String suffix, String full, int amount, boolean upBreak) {
                    this.prefix = prefix;
                    this.suffix = suffix;
                    this.full = full;
                    this.amount = amount;
                    this.upBreak = upBreak;
                }

                public IgnoreBlockInfo() {

                }
            }

            public boolean isPierceable(@NotNull Material material) {
                IgnoreBlockInfo info = getByMaterial(material);

                // 方便理解
                if (info == null) {
                    return false;
                }

                return info.customPierceable;
            }

            public boolean isIgnoreHit(@NotNull Material material) {
                IgnoreBlockInfo info = getByMaterial(material);
                return info != null && info.ignoreHit;
            }

            public IgnoreBlockInfo getByMaterial(@NotNull Material material) {
                String name = material.name().toUpperCase();

                return ignoreBlockInfoList.stream()
                        .filter(info -> (info.full != null && info.full.equalsIgnoreCase(name)) || ((!info.prefix.isEmpty() || !info.suffix.isEmpty()) && name.startsWith(info.prefix) && name.endsWith(info.suffix)))
                        .findFirst()
                        .orElseGet(() -> !material.isSolid() && Config.block.bannedNotSolid ? new IgnoreBlockInfo(null, null, material.name(), 0, false) : null);
            }
        }

        public static final class BlockCustomSetting {
            public final List<CustomBlockInfo> customSettingList = new LinkedList<>();

            public static final class CustomBlockInfo {
                public String type;

                public double hardness;
            }

            public double getHardness(@NotNull Block block) {
                return customSettingList.stream()
                        .filter(customBlockInfo -> customBlockInfo.type.equalsIgnoreCase(block.getType().name()))
                        .map(it -> it.hardness)
                        .findFirst()
                        .orElse(block.getType().getHardness() * Config.defaultBlockHardnessMultiple);
            }
        }
    }

    public static final class RobChestModeSetting {
        public boolean enabled;

        public boolean fixItem;

        public DropItemSetting dropItem;

        public static final class DropItemSetting {
            public final Map<ItemType, Double> itemTypeToDoubleChanceEnumMap = new HashMap<>();

            public final Map<Material, Double> materialToDoubleChanceEnumMap = new HashMap<>();

            public double getChance(ItemStack itemStack) {
                Material type = itemStack.getType();

                if (materialToDoubleChanceEnumMap.containsKey(type)) {
                    return materialToDoubleChanceEnumMap.get(type);
                } else
                    return ItemType.matches(itemStack).getChance();
            }
        }
    }

    public static final class AutoFixResidenceSetting {
        public boolean enabled;

        public long perBlockFixedMills;

        public boolean ignoreEnemy;

        public final List<Material> ignoreBlockTypes = new LinkedList<>();
    }
}

package site.liangbai.realhomehunt.config;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import site.liangbai.realhomehunt.storage.StorageType;
import site.liangbai.realhomehunt.util.ConsoleUtil;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public final class Config {
    private static File configFile;

    public static String prefix;

    public static StorageSetting storage;

    public static ResidenceSetting residence;

    public static BlockSetting block;

    public static List<String> openWorlds;

    public static long maxWaitMills;

    public static long teleportMills;

    public static long unloadPlayerAttackMills;

    public static double gunDamageMultiple;

    public static double defaultBlockHardnessMultiple;

    public static double perPowerLevelDamage;

    public static String consoleLanguage;

    public static long actionBarShowMills;

    public static void init(Plugin plugin) {
        File file = new File(plugin.getDataFolder(), "config.yml");

        if (!file.exists()) {
            plugin.saveDefaultConfig();
        }

        configFile = file;

        reload();
    }

    public static void reload() {
        ConsoleUtil.sendMessage(ChatColor.GREEN + "Reload config...");

        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(configFile);

        prefix = asColored(yamlConfiguration.getString("prefix"));

        openWorlds = yamlConfiguration.getStringList("openWorlds");

        maxWaitMills = yamlConfiguration.getLong("maxWaitMills");

        teleportMills = yamlConfiguration.getLong("teleportMills");

        unloadPlayerAttackMills = yamlConfiguration.getLong("unloadPlayerAttackMills");

        gunDamageMultiple = yamlConfiguration.getDouble("gunDamageMultiple");

        defaultBlockHardnessMultiple = yamlConfiguration.getDouble("defaultBlockHardnessMultiple");

        perPowerLevelDamage = yamlConfiguration.getDouble("perPowerLevelDamage");

        consoleLanguage = yamlConfiguration.getString("consoleLanguage", "zh_cn");

        actionBarShowMills = yamlConfiguration.getLong("actionBarShowMills", 600);

        ConsoleUtil.sendRawMessage(ChatColor.GREEN + "Linking storage settings...");

        linkStorageConfig(yamlConfiguration);

        ConsoleUtil.sendRawMessage(ChatColor.GREEN + "Linking storage settings successful.");

        ConsoleUtil.sendRawMessage(ChatColor.GREEN + "Linking residence settings...");

        linkResidenceConfig(yamlConfiguration);

        ConsoleUtil.sendRawMessage(ChatColor.GREEN + "Linking residence settings successful.");

        ConsoleUtil.sendRawMessage(ChatColor.GREEN + "Linking block settings...");

        linkBlockConfig(yamlConfiguration);

        ConsoleUtil.sendRawMessage(ChatColor.GREEN + "Linking block settings successful.");

        ConsoleUtil.sendRawMessage(ChatColor.GREEN + "Reload config successful.");
    }

    private static void linkResidenceConfig(ConfigurationSection section) {
        ConfigurationSection residenceSection = section.getConfigurationSection("residence");

        if (residenceSection == null) throw new IllegalStateException("can not load config part: residence");

        residence = new ResidenceSetting();

        ConfigurationSection residenceSizeLimitSection = residenceSection.getConfigurationSection("sizeLimit");

        if (residenceSizeLimitSection == null) throw new IllegalStateException("can not load config part: residence.sizeLimit");

        ResidenceSetting.ResidenceSizeSetting sizeSetting = new ResidenceSetting.ResidenceSizeSetting();

        sizeSetting.x = residenceSizeLimitSection.getInt("x");
        sizeSetting.y = residenceSizeLimitSection.getInt("y");
        sizeSetting.z = residenceSizeLimitSection.getInt("z");

        residence.sizeLimit = sizeSetting;

        ConfigurationSection residenceToolSection = residenceSection.getConfigurationSection("tool");

        if (residenceToolSection == null) throw new IllegalStateException("can not load config part: residence.tool");

        ResidenceSetting.ResidenceToolSetting toolSetting = new ResidenceSetting.ResidenceToolSetting();

        String left = residenceToolSection.getString("leftSelect");

        if (left == null) throw new IllegalStateException("can not load config part: residence.tool.leftSelect");

        String right = residenceToolSection.getString("rightSelect");

        if (right == null) throw new IllegalStateException("can not load config part: residence.tool.rightSelect");

        Material leftSelect = Material.matchMaterial(left);

        Material rightSelect = Material.matchMaterial(right);

        toolSetting.leftSelect = leftSelect != null ? leftSelect : Material.STICK;

        toolSetting.rightSelect = rightSelect != null ? rightSelect : Material.STICK;

        residence.tool = toolSetting;
    }

    private static void linkBlockConfig(ConfigurationSection section) {
        ConfigurationSection blockSection = section.getConfigurationSection("block");
        
        if (blockSection == null) throw new IllegalStateException("can not load config part: block");
        
        block = new BlockSetting();
        
        ConfigurationSection blockIgnoreSection = blockSection.getConfigurationSection("ignore");

        BlockSetting.BlockIgnoreSetting ignore = new BlockSetting.BlockIgnoreSetting();

        if (blockIgnoreSection != null) {
            blockIgnoreSection.getKeys(false).stream()
                    .filter(blockIgnoreSection::isConfigurationSection)
                    .map(blockIgnoreSection::getConfigurationSection)
                    .filter(Objects::nonNull)
                    .forEach(it -> {
                        String type = it.getString("type");

                        if (type == null) throw new IllegalStateException("can not load config part: block.ignore." + it + ".type");

                        int amount = it.getInt("amount");

                        boolean upBreak = it.getBoolean("upBreak", false);

                        BlockSetting.BlockIgnoreSetting.IgnoreBlockInfo info = new BlockSetting.BlockIgnoreSetting.IgnoreBlockInfo();

                        info.type = type;

                        info.amount = amount;

                        info.upBreak = upBreak;

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

        String type = storageSection.getString("type");

        storage.type = StorageType.matchStorageType(type);

        ConfigurationSection tableSection = storageSection.getConfigurationSection("table");

        if (tableSection == null) throw new IllegalStateException("can not load config part: storage.table");

        StorageSetting.TableSetting tableSetting = new StorageSetting.TableSetting();

        String residenceTable = tableSection.getString("residenceTable", "residences");

        if (residenceTable == null) throw new IllegalStateException("can not load config part: storage.table.residenceTable");

        tableSetting.residenceTable = residenceTable;

        storage.tableSetting = tableSetting;

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

    public static String asColored(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static final class StorageSetting {
        public StorageType type;

        public TableSetting tableSetting;

        public SqliteSetting sqliteSetting;

        public MySqlSetting mySqlSetting;

        public static final class TableSetting {
            public String residenceTable;
        }

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

        public static final class ResidenceSizeSetting {
            public int x;

            public int y;

            public int z;
        }

        public static final class ResidenceToolSetting {
            public Material leftSelect;

            public Material rightSelect;
        }
    }

    public static final class BlockSetting {
        public BlockIgnoreSetting ignore;
        
        public BlockCustomSetting custom;
        
        public static final class BlockIgnoreSetting {
            public final List<IgnoreBlockInfo> ignoreBlockInfoList = new LinkedList<>();

            public static final class IgnoreBlockInfo {
                public String type;

                public int amount;

                public boolean upBreak;

                public boolean isUpBreak() {
                    return upBreak;
                }
            }

            public boolean contains(@NotNull Material material) {
                for (IgnoreBlockInfo info : ignoreBlockInfoList) {
                    if (info.type.equalsIgnoreCase(material.name())) return true;
                }

                return false;
            }

            public int containsAndReturnLimit(@NotNull Material material) {
                IgnoreBlockInfo info = getByMaterial(material);

                return info != null ? info.amount : -1;
            }

            public IgnoreBlockInfo getByMaterial(@NotNull Material material) {
                for (IgnoreBlockInfo info : ignoreBlockInfoList) {
                    if (info.type.equalsIgnoreCase(material.name())) return info;
                }

                return null;
            }
        }

        public static final class BlockCustomSetting {
            public final List<CustomBlockInfo> customSettingList = new LinkedList<>();

            public static final class CustomBlockInfo {
                public String type;

                public double hardness;
            }

            public double getHardness(@NotNull Block block) {
                for (CustomBlockInfo customBlockInfo : customSettingList) {
                    if (customBlockInfo.type.equalsIgnoreCase(block.getType().name())) return customBlockInfo.hardness;
                }

                return block.getType().getHardness() * Config.defaultBlockHardnessMultiple;
            }
        }
    }
}

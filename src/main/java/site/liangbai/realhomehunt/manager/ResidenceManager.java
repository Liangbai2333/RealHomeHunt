package site.liangbai.realhomehunt.manager;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import site.liangbai.realhomehunt.config.Config;
import site.liangbai.realhomehunt.residence.Residence;
import site.liangbai.realhomehunt.storage.IStorage;
import site.liangbai.realhomehunt.storage.StorageType;
import site.liangbai.realhomehunt.storage.impl.MySqlStorage;
import site.liangbai.realhomehunt.storage.impl.SqliteStorage;
import site.liangbai.realhomehunt.storage.impl.YamlStorage;
import site.liangbai.realhomehunt.util.ConsoleUtil;
import site.liangbai.realhomehunt.util.LocationUtil;

import java.util.LinkedList;
import java.util.List;

public final class ResidenceManager {
    private static final List<Residence> residences = new LinkedList<>();

    private static IStorage storage;

    private static StorageType storageType;

    public static void init(Plugin plugin, StorageType storageType) {
        residences.clear();

        if (storageType == StorageType.SQLITE) {
            storage = new SqliteStorage(plugin, Config.storage.sqliteSetting);
        }

        if (storageType == StorageType.YAML) {
            storage = new YamlStorage(plugin, "data");
        }

        if (storageType == StorageType.MYSQL) {
            storage = new MySqlStorage(Config.storage.mySqlSetting);
        }

        if (storage == null) throw new IllegalStateException("can not receive a null storage type.");

        ResidenceManager.storageType = storageType;

        ConsoleUtil.sendRawMessage(ChatColor.GREEN + "Using storage: " + ChatColor.YELLOW + storageType.name());

        ConsoleUtil.sendRawMessage(ChatColor.GREEN + "Loading player residence data...");

        List<Residence> residenceList = storage.loadAll();

        residences.addAll(residenceList);

        ConsoleUtil.sendRawMessage(ChatColor.GREEN + "Loading player residence data successful.");

        ConsoleUtil.sendRawMessage(ChatColor.GREEN + "Player residence data load info: ");

        int count = storage.count();

        ConsoleUtil.sendRawMessage(ChatColor.GREEN + "  count: " + count);

        ConsoleUtil.sendRawMessage(ChatColor.GREEN + "  success: " + residences.size());

        ConsoleUtil.sendRawMessage(ChatColor.GREEN + "  failed: " + (count - residences.size()));
    }

    public static StorageType getStorageType() {
        return storageType;
    }

    public static void register(Residence residence) {
        register(residence, true);
    }

    public static void register(Residence residence, boolean replaceOld) {
        for (Residence exist : residences) {
            if (exist.getOwner().equals(residence.getOwner())) {
                if (replaceOld) {
                    residences.remove(exist);
                } else return;
            }
        }

        residences.add(residence);
    }

    public static void unregister(Residence residence) {
        residences.remove(residence);
    }

    public static Residence getResidenceByName(String player) {
        for (Residence residence : residences) {
            if (residence.isAdministrator(player)) return residence;
        }

        return null;
    }

    public static Residence getResidenceByOwner(String owner) {
        for (Residence residence : residences) {
            if (residence.isOwner(owner)) return residence;
        }

        return null;
    }

    public static boolean hasResidence(String player) {
        return getResidenceByName(player) != null;
    }

    public static List<Residence> getResidences() {
        return residences;
    }

    public static Residence getResidenceByLocation(Location location) {
        for (Residence value : residences) {
            if (LocationUtil.isInResidence(location, value)) return value;
        }

        return null;
    }

    public static boolean isInResidence(Location location, Residence residence) {
        return LocationUtil.isInResidence(location, residence);
    }

    public static boolean containsResidence(Location loc1, Location loc2) {
        if (getResidenceByLocation(loc1) != null || getResidenceByLocation(loc2) != null) return true;

        for (Residence value : residences) {
            if (LocationUtil.isInZone(value.getLeft(), loc1, loc2) || LocationUtil.isInZone(value.getRight(), loc1, loc2)) return true;
        }

        return false;
    }

    public static boolean isOpened(@NotNull World world) {
        return Config.openWorlds.contains(world.getName());
    }

    public static void save(Residence residence) {
        storage.save(residence);
    }

    public static void remove(Residence residence) {
        storage.remove(residence);

        unregister(residence);
    }
}

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
        getResidences().stream()
                .filter(existResidence -> existResidence.getOwner().equals(residence.getOwner()))
                .findFirst()
                .ifPresent(getResidences()::remove);

        getResidences().add(residence);
    }

    public static void unregister(Residence residence) {
        getResidences().remove(residence);
    }

    public static Residence getResidenceByOwner(String owner) {
        return getResidences().stream()
                .filter(residence -> residence.isOwner(owner))
                .findFirst()
                .orElse(null);
    }

    public static List<Residence> getResidences() {
        return residences;
    }

    public static Residence getResidenceByLocation(Location location) {
        return getResidences().stream()
                .filter(residence -> LocationUtil.isInResidence(location, residence))
                .findFirst()
                .orElse(null);
    }

    public static boolean isInResidence(Location location, Residence residence) {
        return LocationUtil.isInResidence(location, residence);
    }

    public static boolean containsResidence(Location loc1, Location loc2) {
        if (getResidenceByLocation(loc1) != null || getResidenceByLocation(loc2) != null) return true;

        return getResidences().stream()
                .anyMatch(residence -> LocationUtil.isInZone(residence.getLeft(), loc1, loc2) || LocationUtil.isInZone(residence.getRight(), loc1, loc2));
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

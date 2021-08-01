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

package site.liangbai.realhomehunt.api.residence.manager;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import site.liangbai.realhomehunt.config.Config;
import site.liangbai.realhomehunt.api.residence.Residence;
import site.liangbai.realhomehunt.storage.IStorage;
import site.liangbai.realhomehunt.storage.StorageType;
import site.liangbai.realhomehunt.storage.impl.MySqlStorage;
import site.liangbai.realhomehunt.storage.impl.SqliteStorage;
import site.liangbai.realhomehunt.storage.impl.YamlStorage;
import site.liangbai.realhomehunt.util.Console;
import site.liangbai.realhomehunt.util.Locations;

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

        Console.sendRawMessage(ChatColor.GREEN + "Using storage: " + ChatColor.YELLOW + storageType.name());

        Console.sendRawMessage(ChatColor.GREEN + "Loading player residence data...");

        List<Residence> residenceList = storage.loadAll();

        residences.addAll(residenceList);

        Console.sendRawMessage(ChatColor.GREEN + "Loading player residence data successful.");

        Console.sendRawMessage(ChatColor.GREEN + "Player residence data load info: ");

        int count = storage.count();

        Console.sendRawMessage(ChatColor.GREEN + "  count: " + count);

        Console.sendRawMessage(ChatColor.GREEN + "  success: " + residences.size());

        Console.sendRawMessage(ChatColor.GREEN + "  failed: " + (count - residences.size()));
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
                .filter(residence -> Locations.isInResidence(location, residence))
                .findFirst()
                .orElse(null);
    }

    public static boolean isInResidence(Location location, Residence residence) {
        return Locations.isInResidence(location, residence);
    }

    public static boolean containsResidence(Location loc1, Location loc2) {
        if (getResidenceByLocation(loc1) != null || getResidenceByLocation(loc2) != null) return true;

        return getResidences().stream()
                .anyMatch(residence -> Locations.isInZone(residence.getLeft(), loc1, loc2) || Locations.isInZone(residence.getRight(), loc1, loc2));
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
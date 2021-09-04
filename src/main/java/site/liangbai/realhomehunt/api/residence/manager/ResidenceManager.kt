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

package site.liangbai.realhomehunt.api.residence.manager

import com.bekvon.bukkit.residence.api.ResidenceApi
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.plugin.Plugin
import site.liangbai.realhomehunt.api.residence.Residence
import site.liangbai.realhomehunt.common.config.Config
import site.liangbai.realhomehunt.internal.storage.IStorage
import site.liangbai.realhomehunt.internal.storage.StorageType
import site.liangbai.realhomehunt.internal.storage.impl.MySQLStorage
import site.liangbai.realhomehunt.internal.storage.impl.SqliteStorage
import site.liangbai.realhomehunt.internal.storage.impl.YamlStorage
import site.liangbai.realhomehunt.util.Console
import site.liangbai.realhomehunt.util.contains

internal object ResidenceManager {
    private val ownerToResidenceMap = hashMapOf<String, Residence>()
    lateinit var storage: IStorage
    lateinit var storageType: StorageType
    var enabled = false

    fun init(plugin: Plugin, storageType: StorageType) {
        ownerToResidenceMap.clear()
        if (storageType == StorageType.SQLITE) {
            storage = SqliteStorage(plugin, Config.storage.sqliteSetting)
        }
        if (storageType == StorageType.YAML) {
            storage = YamlStorage(plugin, "data")
        }
        if (storageType == StorageType.MYSQL) {
            storage = MySQLStorage(Config.storage.mySqlSetting)
        }
        ResidenceManager.storageType = storageType
        Console.sendRawMessage(ChatColor.GREEN.toString() + "Using storage: " + ChatColor.YELLOW + storageType.name)
        Console.sendRawMessage(ChatColor.GREEN.toString() + "Loading player residence data...")
        val residenceList: List<Residence> = storage.loadAll()
        residenceList.forEach {
            ownerToResidenceMap[it.owner] = it
        }
        Console.sendRawMessage(ChatColor.GREEN.toString() + "Loading player residence data successful.")
        Console.sendRawMessage(ChatColor.GREEN.toString() + "Player residence data load info: ")
        val count = storage.count()
        Console.sendRawMessage(ChatColor.GREEN.toString() + "  count: " + count)
        Console.sendRawMessage(ChatColor.GREEN.toString() + "  success: " + ownerToResidenceMap.size)
        Console.sendRawMessage(ChatColor.GREEN.toString() + "  failed: " + (count - ownerToResidenceMap.size))
        enabled = true
    }

    fun register(residence: Residence) {
        ownerToResidenceMap[residence.owner] = residence
    }

    fun unregister(residence: Residence) {
        ownerToResidenceMap.remove(residence.owner)
    }

    @JvmStatic
    fun getResidenceByOwner(owner: String): Residence? {
        return ownerToResidenceMap[owner]
    }

    fun getResidences(): MutableCollection<Residence> {
        return ownerToResidenceMap.values
    }

    fun getResidenceByLocation(location: Location): Residence? {
        return getResidences().stream()
            .parallel()
            .filter { location in it.left to it.right }
            .findAny()
            .orElse(null)
    }

    @JvmStatic
    fun isInResidence(location: Location, residence: Residence): Boolean {
        return location in residence
    }

    fun hasResidence(loc1: Location, loc2: Location): Boolean {
        if (getResidenceByLocation(loc1) != null || getResidenceByLocation(loc2) != null) return true

        if (getResidences().stream()
                .parallel()
                .anyMatch {
                    it.left in loc1 to loc2 || it.right in loc1 to loc2
                }) return true



        if (Config.residence.bannedCreateInResidence && Bukkit.getPluginManager().isPluginEnabled("Residence")) {
            val manager = ResidenceApi.getResidenceManager() as com.bekvon.bukkit.residence.protection.ResidenceManager

            if (manager.getByLoc(loc1) != null || manager.getByLoc(loc2) != null) return true

            return manager.residences.values.stream()
                .parallel()
                .map { it.areaArray }
                .anyMatch {
                    for (zone in it) {
                        if (zone.lowLocation in loc1 to loc2 || zone.highLocation in loc1 to loc2) return@anyMatch true
                    }

                    return@anyMatch false
                }
        }

        return false
    }

    @JvmStatic
    fun isOpened(world: World): Boolean {
        return world.name in Config.openWorlds
    }

    @JvmStatic
    fun save(residence: Residence) {
        storage.save(residence)
    }

    @JvmStatic
    fun remove(residence: Residence) {
        storage.remove(residence)
        unregister(residence)
    }
}
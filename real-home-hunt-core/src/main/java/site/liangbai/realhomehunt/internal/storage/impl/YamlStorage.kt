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

package site.liangbai.realhomehunt.internal.storage.impl

import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import site.liangbai.realhomehunt.api.residence.Residence
import site.liangbai.realhomehunt.internal.storage.IStorage
import java.io.File
import java.io.IOException

class YamlStorage(plugin: Plugin, path: String) : IStorage {
    private val dataFolder: File
    private val playerData: Array<File>?
    @Throws(IOException::class)
    fun getResidenceFile(residence: Residence): File {
        val file = File(dataFolder, residence.owner + ".yml")
        if (!file.exists()) {
            check(file.createNewFile()) { "can not create new residence file: " + residence.owner + ".yml" }
        }
        return file
    }

    override fun save(residence: Residence) {
        try {
            val file = getResidenceFile(residence)
            val yamlConfiguration = YamlConfiguration.loadConfiguration(file)
            yamlConfiguration["residence"] = residence
            yamlConfiguration.save(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun remove(residence: Residence) {
        try {
            val file = getResidenceFile(residence)
            check(file.delete()) { "can not delete residence file: " + residence.owner + ".yml" }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun loadAll(): List<Residence> {
        return playerData!!
            .filter { it.name.endsWith(".yml") }
            .map {
                val config = YamlConfiguration()
                try {
                    config.load(it)
                } catch (ignored: IOException) {
                } catch (ignored: InvalidConfigurationException) {
                }
                config
            }
            .filter { it.contains("residence") }
            .mapNotNull { it.getObject("residence", Residence::class.java) }
    }

    override fun count(): Long {
        return playerData!!.size.toLong()
    }

    override fun close() {
        // TODO
    }

    init {
        dataFolder = File(plugin.dataFolder, path)
        if (!dataFolder.exists()) {
            check(dataFolder.mkdirs()) { "can not create new folder: " + dataFolder.absolutePath }
        }
        playerData = dataFolder.listFiles()
        checkNotNull(playerData) { "can not load data in file." }
    }
}
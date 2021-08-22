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
package site.liangbai.realhomehunt.internal.storage.impl

import com.dieselpoint.norm.Database
import site.liangbai.realhomehunt.api.residence.Residence
import site.liangbai.realhomehunt.internal.storage.IStorage
import site.liangbai.realhomehunt.util.kt.createTableIfNotExists

abstract class SqlStorage : IStorage {
    private val owners = mutableListOf<String>()

    private var count: Long = 0
    abstract val database: Database
    fun initTable() {
        database.createTableIfNotExists(Residence::class.java)
    }

    @Synchronized
    override fun save(residence: Residence) {
        if (!owners.contains(residence.owner)) {
            database.insert(residence)
            owners.add(residence.owner)
            return
        }
        database.update(residence)
    }

    @Synchronized
    override fun remove(residence: Residence) {
        if (owners.contains(residence.owner)) {
            database.delete(residence)
            owners.remove(residence.owner)
        }
    }

    override fun loadAll(): List<Residence> {
        val list = database.results(
            Residence::class.java
        ).filterNotNull()
        list.map { it.owner }
            .forEach { owners.add(it) }
        count = list.size.toLong()
        return list
    }

    override fun count(): Long {
        return count
    }

    override fun close() {
        database.close()
    }
}
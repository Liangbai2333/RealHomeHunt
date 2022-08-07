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

import site.liangbai.realhomehunt.api.residence.Residence
import site.liangbai.realhomehunt.common.database.ConverterManager.convertToEntity
import site.liangbai.realhomehunt.common.database.ConverterManager.convertToEntityList
import site.liangbai.realhomehunt.common.database.ConverterManager.convertToString
import site.liangbai.realhomehunt.internal.storage.IStorage
import taboolib.module.database.ColumnBuilder
import taboolib.module.database.Host
import taboolib.module.database.Table

abstract class SqlStorage<T: Host<E>, E : ColumnBuilder> : IStorage {
    abstract val host: Host<E>

    abstract val table: Table<T, E>

    val dataSource by lazy {
        host.createDataSource()
    }
    private var count = 0L
    private val cache = mutableListOf<String>()

    override fun remove(residence: Residence) {
        table.workspace(dataSource) {
            delete { where { "owner" eq residence.owner } }
        }.run()

        cache.remove(residence.owner)
    }

    override fun save(residence: Residence) {
        if (residence.owner in cache) {
            table.workspace(dataSource) {
                update {
                    set("left", residence.left.convertToString())
                    set("right", residence.right.convertToString())
                    set("administrators", residence.administrators.convertToString())
                    set("attributes", residence.attributes.convertToString())
                    set("ignoreBlockCounterList", residence.ignoreBlockCounterList.convertToString())
                    set("spawn", residence.spawn.convertToString())
                    where {
                        "owner" eq residence.owner
                    }
                }
            }.run()
        } else {
            table.workspace(dataSource) {
                insert("owner", "left", "right", "administrators", "attributes", "ignoreBlockCounterList", "spawn") {
                    value(
                        residence.owner,
                        residence.left.convertToString(),
                        residence.right.convertToString(),
                        residence.administrators.convertToString(),
                        residence.attributes.convertToString(),
                        residence.ignoreBlockCounterList.convertToString(),
                        residence.spawn.convertToString()
                    )
                }
            }.run()

            cache.add(residence.owner)
        }
    }

    override fun loadAll(): List<Residence> {
        return table.workspace(dataSource) {
            select {  }
        }.map {
            val residence = Residence()
            residence.owner = getString("owner")
            residence.left = getString("left").convertToEntity()
            residence.right = getString("right").convertToEntity()
            residence.administrators = getString("administrators").convertToEntityList()
            residence.attributes = getString("attributes").convertToEntityList()
            residence.ignoreBlockCounterList = getString("ignoreBlockCounterList").convertToEntityList()
            residence.spawn = getString("spawn").convertToEntity()
            count++
            cache.add(residence.owner)

            residence
        }
    }

    override fun count(): Long {
        return count
    }

    override fun close() {
        dataSource.connection.close()
    }
}
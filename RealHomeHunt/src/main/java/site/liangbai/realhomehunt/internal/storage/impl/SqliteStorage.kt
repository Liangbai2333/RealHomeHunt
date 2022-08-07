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

import org.bukkit.plugin.Plugin
import site.liangbai.realhomehunt.common.config.Config.StorageSetting.SqliteSetting
import taboolib.common.io.newFile
import taboolib.module.database.*

class SqliteStorage(plugin: Plugin, setting: SqliteSetting) : SqlStorage<Host<SQLite>, SQLite>() {
    override val host = newFile(if (setting.onlyInPluginFolder) plugin.dataFolder.absolutePath + "/" + setting.databaseFile else setting.databaseFile).getHost()

    override val table = Table("residences", host) {
        add {
            name("owner")
            type(ColumnTypeSQLite.TEXT, 36) {
                options(ColumnOptionSQLite.PRIMARY_KEY)
            }
        }

        add {
            name("left")
            type(ColumnTypeSQLite.TEXT)
        }

        add {
            name("right")
            type(ColumnTypeSQLite.TEXT)
        }

        add {
            name("administrators")
            type(ColumnTypeSQLite.TEXT)
        }

        add {
            name("attributes")
            type(ColumnTypeSQLite.TEXT)
        }

        add {
            name("ignoreBlockCounterList")
            type(ColumnTypeSQLite.TEXT)
        }

        add {
            name("spawn")
            type(ColumnTypeSQLite.TEXT)
        }
    }

    init {
        table.workspace(dataSource) { createTable(true) }.run()
    }
}
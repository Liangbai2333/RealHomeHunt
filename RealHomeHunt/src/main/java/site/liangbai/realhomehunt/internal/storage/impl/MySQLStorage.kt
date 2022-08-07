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

import site.liangbai.realhomehunt.common.config.Config
import taboolib.module.database.*

class MySQLStorage(setting: Config.StorageSetting.MySqlSetting) : SqlStorage<Host<SQL>, SQL>() {
    override val host = HostSQL(setting.address, setting.port.toString(), setting.user, setting.password, setting.database)

    override val table = Table("residences", host) {
        add {
            name("owner")
            type(ColumnTypeSQL.TEXT, 36) {
                options(ColumnOptionSQL.PRIMARY_KEY)
            }
        }

        add {
            name("left")
            type(ColumnTypeSQL.TEXT)
        }

        add {
            name("right")
            type(ColumnTypeSQL.TEXT)
        }

        add {
            name("administrators")
            type(ColumnTypeSQL.TEXT)
        }

        add {
            name("attributes")
            type(ColumnTypeSQL.TEXT)
        }

        add {
            name("ignoreBlockCounterList")
            type(ColumnTypeSQL.TEXT)
        }

        add {
            name("spawn")
            type(ColumnTypeSQL.TEXT)
        }
    }

    init {
        if (setting.options.isNotEmpty()) {
            host.flags.add(setting.options)
        }

        table.workspace(dataSource) { createTable(true) }.run()
    }
}
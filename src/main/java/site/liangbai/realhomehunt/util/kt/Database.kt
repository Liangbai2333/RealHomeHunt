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

package site.liangbai.realhomehunt.util.kt

import com.dieselpoint.norm.Database
import com.dieselpoint.norm.Query
import com.dieselpoint.norm.sqlmakers.StandardSqlMaker
import site.liangbai.realhomehunt.util.SqlMaker

fun Database.createTableIfNotExists(table: Class<*>) {
    val sqlMaker = sqlMaker as StandardSqlMaker
    val proxy = SqlMaker(sqlMaker)
    Query(this).sql(proxy.getCreateTableIfNotExistsSql(table)).execute()
}
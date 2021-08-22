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
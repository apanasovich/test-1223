package db

import java.sql.Connection

fun Connection.select(q: String, vararg args: Any): List<Map<String, Any?>> {
    val stmt = prepareStatement(q)
    args.forEachIndexed { i, v -> stmt.setObject(i + 1, v) }
    val result = mutableListOf<Map<String, Any?>>()
    stmt.executeQuery().use { resultSet ->
        val metaData = resultSet.metaData
        val columnCount = metaData.columnCount
        while (resultSet.next()) {
            val map = mutableMapOf<String, Any?>()
            for (col in 1..columnCount) {
                map[metaData.getColumnLabel(col).toUpperCase()] = resultSet.getObject(col)
            }
            result += map
        }
    }
    return result
}

fun Connection.call(q: String, vararg args: Any) {
    val stmt = prepareCall(q)
    args.forEachIndexed { i, v -> stmt.setObject(i + 1, v) }
    stmt.execute()
}

fun Connection.update(q: String, vararg args: Any): Int {
    val stmt = prepareCall(q)
    args.forEachIndexed { i, v -> stmt.setObject(i + 1, v) }
    return stmt.executeUpdate()
}
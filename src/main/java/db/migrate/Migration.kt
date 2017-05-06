package db.migrate

import db.call
import db.select
import db.update
import java.sql.Connection
import java.util.*

private val versions = TreeMap<Int, Pair<Connection.() -> Unit, Connection.() -> Unit>>()

fun register(version: Int, up: Connection.() -> Unit, down: Connection.() -> Unit) {
    versions[version] = up to down
}

fun migrate(conn: Connection, version: Int) {
    versions.filterKeys { it > version }.entries.reversed().forEach { (v, actions) ->
        val (_, down) = actions
        conn.down()
        setCurrentVersion(conn, v)
    }
    versions.filterKeys { it <= version }.forEach { (v, actions) ->
        val (up, _) = actions
        conn.up()
        setCurrentVersion(conn, v)
    }
}

private fun getCurrentVersion(conn: Connection): Int {
    ensureDbVersionTableExists(conn)
    val row = conn.select("SELECT V FROM APP_SETTINGS.DB_VERSION").firstOrNull()
    if (row == null) {
        conn.update("INSERT INTO APP_SETTINGS.DB_VERSION (V) VALUES (?)", 0)
        return 0
    } else {
        return row["V"] as Int
    }
}

private fun setCurrentVersion(conn: Connection, v: Int) {
    ensureDbVersionTableExists(conn)
    conn.update("UPDATE APP_SETTINGS.DB_VERSION SET V = ?", v)
}

private fun ensureDbVersionTableExists(conn: Connection) {
    conn.call("CREATE SCHEMA IF NOT EXISTS APP_SETTINGS")
    conn.call("CREATE TABLE IF NOT EXISTS APP_SETTINGS.DB_VERSION (V INTEGER)")
}
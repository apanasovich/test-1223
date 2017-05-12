package db

import java.sql.Connection
import java.util.*

object Versions {
    const val TARGET_VERSION = 4

    private val versions = TreeMap<Int, Pair<Connection.() -> Unit, Connection.() -> Unit>>()

    init {
        registerFromDir("db", 1)
        registerFromDir("db", 2)
        registerFromDir("db", 3)
        registerFromDir("db", 4)
    }

    val max = if (versions.isEmpty()) 0 else versions.lastKey()!!

    fun register(version: Int, up: Connection.() -> Unit, down: Connection.() -> Unit) {
        versions[version] = up to down
    }

    fun registerFromDir(path: String, version: Int) {
        val up: Connection.() -> Unit = { batch("$path/$version/up.sql") }
        val down: Connection.() -> Unit = { batch("$path/$version/down.sql") }
        register(version, up, down)
    }

    fun migrate(conn: Connection, version: Int) {
        versions.filterKeys { it > version }.entries.reversed().forEach { (v, actions) ->
            if (getCurrent(conn) == v) {
                synchronized(this) {
                    if (getCurrent(conn) == v) {
                        val (_, down) = actions
                        conn.down()
                        setCurrent(conn, v - 1)
                    }
                }
            }
        }
        versions.filterKeys { it <= version }.forEach { (v, actions) ->
            if (getCurrent(conn) == v - 1) {
                synchronized(this) {
                    if (getCurrent(conn) == v - 1) {
                        val (up, _) = actions
                        conn.up()
                        setCurrent(conn, v)
                    }
                }
            }
        }
    }

    fun getCurrent(conn: Connection): Int {
        ensureDbVersionTableExists(conn)
        val row = conn.select("SELECT V FROM APP_SETTINGS.DB_VERSION").firstOrNull()
        if (row == null) {
            conn.update("INSERT INTO APP_SETTINGS.DB_VERSION (V) VALUES (?)", 0)
            return 0
        } else {
            return row["V"] as Int
        }
    }

    private fun setCurrent(conn: Connection, v: Int) {
        ensureDbVersionTableExists(conn)
        conn.update("UPDATE APP_SETTINGS.DB_VERSION SET V = ?", v)
    }

    private fun ensureDbVersionTableExists(conn: Connection) {
        conn.call("CREATE SCHEMA IF NOT EXISTS APP_SETTINGS")
        conn.call("CREATE TABLE IF NOT EXISTS APP_SETTINGS.DB_VERSION (V INTEGER)")
    }
}

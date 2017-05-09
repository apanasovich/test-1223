package db

import org.junit.Test
import kotlin.test.assertEquals

class MigrationTest : DbTest() {
    @Test
    fun testMigration() {
        val max = Versions.max

        assertEquals(0, Versions.getCurrent(conn))

        val range = (1..max).toList()

        for (v in range) {
            Versions.migrate(conn, v)
            assertEquals(v, Versions.getCurrent(conn))
        }

        for (v in range.reversed().map { it - 1 }) {
            Versions.migrate(conn, v)
            assertEquals(v, Versions.getCurrent(conn))
        }

        assertEquals(0, Versions.getCurrent(conn))
    }
}
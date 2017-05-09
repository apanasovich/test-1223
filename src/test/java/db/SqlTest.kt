package db

import org.junit.Test
import kotlin.test.assertEquals

class SqlTest : DbTest() {
    @Test
    fun testBatch() {
        conn.batch("db/1/up.sql")

        conn.update("INSERT INTO TASKS.TASKS (SUMMARY, DESCRIPTION) VALUES (?, ?)",
                "Some Task",
                "Do this and that"
        )

        val list = conn.select("SELECT * FROM TASKS.TASKS")
        assertEquals(1, list.size)
        assertEquals(1, list[0]["ID"])
        assertEquals("Some Task", list[0]["SUMMARY"])
        assertEquals("Do this and that", list[0]["DESCRIPTION"])
    }
}
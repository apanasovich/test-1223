package db

import org.junit.After
import org.junit.Before
import java.sql.Connection
import java.sql.DriverManager

abstract class DbTest {
    protected lateinit var conn: Connection

    @Before
    fun setUp() {
        conn = DriverManager.getConnection("jdbc:h2:mem:test_mem;MODE=PostgreSQL");
    }

    @After
    fun tearDown() {
        conn.close()
    }
}
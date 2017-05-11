package servlet

import com.fasterxml.jackson.databind.ObjectMapper
import com.mchange.v2.c3p0.ComboPooledDataSource
import db.Versions
import java.sql.Connection
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

abstract class ServletBase : HttpServlet() {
    protected val mapper = ObjectMapper()

    protected fun connect(): Connection {
        val connection = cpds.connection
        Versions.migrate(connection, Versions.TARGET_VERSION)
        return connection
    }

    protected fun HttpServletRequest.requiredArg(name: String) =
            (getParameter(name) ?: throw IllegalArgumentException("'$name' is not provided")).also {
                if (it.isBlank()) throw IllegalArgumentException("'$name' should not be blank")
            }

    protected fun HttpServletResponse.sendJsonOutput(o: Any) = also {
        setHeader("Content-Type", "application/json;charset=utf-8")
        outputStream.use { out ->
            mapper.writeValue(out, o)
            out.flush()
        }
    }

    companion object {
        private val cpds = ComboPooledDataSource().also {
            it.jdbcUrl = System.getenv("JDBC_DATABASE_URL")
            it.maxPoolSize = 10
        }
    }
}

package servlet

import com.fasterxml.jackson.databind.ObjectMapper
import com.mchange.v2.c3p0.ComboPooledDataSource
import db.Versions
import org.slf4j.LoggerFactory
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

    protected fun HttpServletResponse.sendJsonOutput(o: Any?) = also {
        setHeader("Content-Type", "application/json;charset=utf-8")
        outputStream.use { out ->
            mapper.writeValue(out, o)
            out.flush()
        }
    }

    final override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) = try {
        post(req, resp)
    } catch (e: Throwable) {
        handleError(e, resp)
    }

    final override fun doDelete(req: HttpServletRequest, resp: HttpServletResponse) = try {
        delete(req, resp)
    } catch (e: Throwable) {
        handleError(e, resp)
    }

    final override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) = try {
        get(req, resp)
    } catch (e: Throwable) {
        handleError(e, resp)
    }

    final override fun doPut(req: HttpServletRequest, resp: HttpServletResponse) = try {
        put(req, resp)
    } catch (e: Throwable) {
        handleError(e, resp)
    }

    private fun handleError(e: Throwable, resp: HttpServletResponse) {
        log.error("Error: ", e)
        resp.status = 500
        resp.sendJsonOutput(mapOf("error" to e.message))
    }

    open fun post(req: HttpServletRequest, resp: HttpServletResponse): Unit = throw UnsupportedOperationException("post")
    open fun delete(req: HttpServletRequest, resp: HttpServletResponse): Unit = throw UnsupportedOperationException("delete")
    open fun get(req: HttpServletRequest, resp: HttpServletResponse): Unit = throw UnsupportedOperationException("get")
    open fun put(req: HttpServletRequest, resp: HttpServletResponse): Unit = throw UnsupportedOperationException("put")

    companion object {
        private val log = LoggerFactory.getLogger(ServletBase::class.java)

        private val cpds = ComboPooledDataSource().also {
            it.jdbcUrl = System.getenv("JDBC_DATABASE_URL")
            it.maxPoolSize = 10
        }
    }
}

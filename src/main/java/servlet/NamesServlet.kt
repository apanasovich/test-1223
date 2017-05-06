package servlet

import com.fasterxml.jackson.databind.ObjectMapper
import db.call
import db.select
import db.update
import java.io.IOException
import java.sql.DriverManager
import javax.servlet.ServletException
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(name = "NamesServlet", urlPatterns = arrayOf("/names"))
class NamesServlet : HttpServlet() {
    private val mapper = ObjectMapper()

    @Throws(ServletException::class, IOException::class)
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.setHeader("Content-Type", "application/json;charset=utf-8")

        val names = mutableListOf<String>()

        DriverManager.getConnection(System.getenv("JDBC_DATABASE_URL")).use {
            it.call("CREATE SCHEMA IF NOT EXISTS TEST1223")
            it.call("CREATE TABLE IF NOT EXISTS TEST1223.ALL_NAMES (NAME VARCHAR(50))")
            it.select("SELECT NAME FROM TEST1223.ALL_NAMES ORDER BY NAME").forEach { names.add(it["NAME"].toString()) }
        }

        resp.outputStream.use { out ->
            mapper.writeValue(out, names)
            out.flush()
        }
    }

    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.setHeader("Content-Type", "application/json;charset=utf-8")

        DriverManager.getConnection(System.getenv("JDBC_DATABASE_URL")).use {
            it.call("CREATE SCHEMA IF NOT EXISTS TEST1223")
            it.call("CREATE TABLE IF NOT EXISTS TEST1223.ALL_NAMES (NAME VARCHAR(50))")

            val count = it.update(
                    "INSERT INTO TEST1223.ALL_NAMES VALUES(?)",
                    req.getParameter("value") ?: throw IllegalArgumentException("'value' not provided"))
            resp.outputStream.use { out ->
                mapper.writeValue(out, mapOf("inserted" to count))
                out.flush()
            }
        }
    }
}

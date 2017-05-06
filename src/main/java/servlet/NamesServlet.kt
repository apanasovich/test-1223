package servlet

import com.fasterxml.jackson.databind.ObjectMapper
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
            it.prepareCall("CREATE SCHEMA IF NOT EXISTS TEST1223").execute()
            it.prepareCall("CREATE TABLE IF NOT EXISTS TEST1223.ALL_NAMES (NAME VARCHAR(50))").execute()

            val rs = it.prepareStatement("SELECT NAME FROM TEST1223.ALL_NAMES ORDER BY NAME").executeQuery()
            rs.use {
                while (rs.next()) {
                    names += rs.getString(1)
                }
            }
        }

        resp.outputStream.use { out ->
            mapper.writeValue(out, names)
            out.flush()
        }
    }

    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.setHeader("Content-Type", "application/json;charset=utf-8")

        DriverManager.getConnection(System.getenv("JDBC_DATABASE_URL")).use {
            it.prepareCall("CREATE SCHEMA IF NOT EXISTS TEST1223").execute()
            it.prepareCall("CREATE TABLE IF NOT EXISTS TEST1223.ALL_NAMES (NAME VARCHAR(50))").execute()

            val call = it.prepareCall("INSERT INTO TEST1223.ALL_NAMES VALUES(?)")
            call.setString(1, req.getParameter("value")!!)
            resp.outputStream.use { out ->
                mapper.writeValue(out, mapOf("inserted" to call.executeUpdate()))
                out.flush()
            }
        }
    }
}

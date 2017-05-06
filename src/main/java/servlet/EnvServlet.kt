package servlet

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(name = "EnvServlet", urlPatterns = arrayOf("/env"))
class EnvServlet : HttpServlet() {
    private val mapper = ObjectMapper()

    @Throws(ServletException::class, IOException::class)
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.setHeader("Content-Type", "application/json;charset=utf-8")

        resp.outputStream.use { out ->
            mapper.writeValue(out, System.getenv())
            out.flush()
        }
    }
}

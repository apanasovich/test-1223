package servlet

import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(name = "MyServlet", urlPatterns = arrayOf("/hello"))
class HelloServlet : HttpServlet() {

    @Throws(ServletException::class, IOException::class)
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.setHeader("Content-Type", "text/html;charset=utf-8")
        val out = resp.outputStream
        out.write("Hello, ${req.getParameter("name")}!".toByteArray())
        out.flush()
        out.close()
    }
}
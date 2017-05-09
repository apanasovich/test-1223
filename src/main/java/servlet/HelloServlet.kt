package servlet

import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(name = "HelloServlet", urlPatterns = arrayOf("/hello"))
class HelloServlet : ServletBase() {
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.sendJsonOutput(mapOf("msg" to "Боброго времени суток тебе, ${req.getParameter("name")}!"))
    }
}

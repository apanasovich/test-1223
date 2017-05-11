package servlet

import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(name = "EnvServlet", urlPatterns = arrayOf("/env"))
class EnvServlet : ServletBase() {
    override fun get(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.sendJsonOutput(System.getenv())
    }
}

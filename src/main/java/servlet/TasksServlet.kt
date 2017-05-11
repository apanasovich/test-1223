package servlet

import db.insertReturning
import db.select
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(name = "TasksServlet", urlPatterns = arrayOf("/tasks"))
class TasksServlet : ServletBase() {
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.sendJsonOutput(connect().use {
            it.select("SELECT * FROM TASKS.TASKS ORDER BY ID")
        })
    }

    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        connect().use {
            val id = it.insertReturning(
                    "INSERT INTO TASKS.TASKS(SUMMARY, DESCRIPTION) VALUES(?, ?) RETURNING ID",
                    req.requiredArg("summary"),
                    req.requiredArg("description"))

            resp.sendJsonOutput(mapOf("ID" to id))
        }
    }
    
    override fun doDelete(req: HttpServletRequest, resp: HttpServletResponse) {
        connect().use {
            val count = it.update("DELETE FROM TASKS.TASKS WHERE ID = ?", req.requiredArg("id"))
            resp.sendJsonOutput(mapOf("DELETED" to count))
        }
    }
}

package servlet

import db.insertReturning
import db.select
import db.update
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(name = "TasksServlet", urlPatterns = arrayOf("/tasks"))
class TasksServlet : ServletBase() {
    override fun get(req: HttpServletRequest, resp: HttpServletResponse) {
        val id = req.getParameter("id")
        val query = req.getParameter("query")
        var result: Any? = null
        if (id != null) {
            result = connect().use {
                mapOf("task" to it.select("SELECT * FROM TASKS.TASKS WHERE ID = ?", (id as String).toInt()).stream().findFirst().orElse(null))
            }
        } else if (query != null) {
            result = connect().use {
                it.select("SELECT * FROM TASKS.TASKS WHERE SUMMARY LIKE '%${query}%' ORDER BY ID")
            }
        } else {
            result = connect().use {
                it.select("SELECT * FROM TASKS.TASKS ORDER BY ID")
            }
        }
        resp.sendJsonOutput(result);
    }

    override fun post(req: HttpServletRequest, resp: HttpServletResponse) {
        connect().use {
            val id = it.insertReturning(
                    "INSERT INTO TASKS.TASKS(SUMMARY, DESCRIPTION) VALUES(?, ?) RETURNING ID",
                    req.requiredArg("summary"),
                    req.requiredArg("description"))

            resp.sendJsonOutput(mapOf("ID" to id))
        }
    }

    override fun delete(req: HttpServletRequest, resp: HttpServletResponse) {
        connect().use {
            val count = it.update("DELETE FROM TASKS.TASKS WHERE ID = ?", req.requiredArg("id").toInt())
            resp.sendJsonOutput(mapOf("DELETED" to count))
        }
    }
}

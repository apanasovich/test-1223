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
        val id = req.getParameter("id").takeUnless { it.isNullOrBlank() }?.toInt() ?: 0
        val query = req.getParameter("query")?.replace(' ', '%')?.toUpperCase()
        val result: Any = connect().use {
            when {
                id != 0 ->
                    mapOf("task" to it.select("SELECT * FROM TASKS.TASKS WHERE ID = ?", id).firstOrNull())
                query != null ->
                    it.select("SELECT * FROM TASKS.TASKS WHERE upper(SUMMARY) LIKE '%$query%' OR upper(DESCRIPTION) LIKE '%$query%' ORDER BY ID")
                else ->
                    it.select("SELECT * FROM TASKS.TASKS ORDER BY ID")
            }
        }
        resp.sendJsonOutput(result);
    }

    override fun post(req: HttpServletRequest, resp: HttpServletResponse) {
        connect().use {
            val id = it.insertReturning(
                    "INSERT INTO TASKS.TASKS(SUMMARY, DESCRIPTION, DONE) VALUES(?, ?, ?) RETURNING ID",
                    req.requiredArg("summary"),
                    req.requiredArg("description"),
                    req.getParameter("done") != "false")

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

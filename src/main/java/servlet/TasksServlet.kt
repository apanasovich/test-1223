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
        val id = req.optionalArg("id")?.toInt() ?: 0
        val query = req.optionalArg("query")?.replace(' ', '%')?.toUpperCase()
        val result: Any = connect().use {
            when {
                id != 0 ->
                    mapOf("task" to it.select("SELECT * FROM TASKS.TASKS WHERE ID = ?", id).firstOrNull())
                query != null ->
                    it.select("SELECT * FROM TASKS.TASKS WHERE upper(SUMMARY) LIKE '%$query%' OR upper(DESCRIPTION) LIKE '%$query%' ORDER BY DONE DESC, ID")
                else ->
                    it.select("SELECT * FROM TASKS.TASKS ORDER BY DONE DESC, ID")
            }
        }
        resp.sendJsonOutput(result);
    }

    override fun post(req: HttpServletRequest, resp: HttpServletResponse) {
        connect().use {
            var id = req.optionalArg("id")?.toInt()

            if (id == null) {
                id = it.insertReturning(
                        "INSERT INTO TASKS.TASKS(SUMMARY, DESCRIPTION, DONE) VALUES(?, ?, ?) RETURNING ID",
                        req.requiredArg("summary"),
                        req.requiredArg("description"),
                        req.getParameter("done") != "false")
            } else {
                val fields = mutableMapOf<String, Any>()
                req.optionalArg("summary")?.also { fields["SUMMARY"] = it }
                req.optionalArg("description")?.also { fields["DESCRIPTION"] = it }
                req.optionalArg("done")?.also { fields["DONE"] = it != "false" }
                it.update("""
                    UPDATE TASKS.TASK SET ${fields.keys.map { "$it = ?" }.joinToString(separator = ", ")}
                    WHERE ID = ?
                """, *(ArrayList(fields.values).also { it += id }.toTypedArray()))
            }

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

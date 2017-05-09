package servlet

import db.select
import db.update
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
            val count = it.update(
                    "INSERT INTO TASKS.TASKS(ID, SUMMARY, DESCRIPTION) " +
                            "VALUES(TASKS.TASKS_SEQ.nextval, ?, ?)",
                    req.requiredArg("summary"),
                    req.requiredArg("description"))

            resp.sendJsonOutput(mapOf("inserted" to count))
        }
    }
}

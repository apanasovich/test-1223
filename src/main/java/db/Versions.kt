package db

import db.migrate.register

object Versions {
    init {
        register(
                version = 1,
                up = {
                    call("CREATE SCHEMA TASKS")
                },
                down = {
                    call("DROP SCHEMA TASKS")
                }
        )
    }
}
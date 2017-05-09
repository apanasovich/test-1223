create schema TASKS
/

create table TASKS.TASKS (
   ID VARCHAR(50) PRIMARY KEY,
   SUMMARY VARCHAR(100),
   DESCRIPTION VARCHAR(100)
)
/

create sequence TASKS.TASKS_SEQ
/
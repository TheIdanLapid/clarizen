package com.clarizen.taskContainer.dao;

import com.clarizen.taskContainer.controller.TaskContainerController;
import com.clarizen.taskContainer.exceptions.DataNotFoundInDBException;
import com.clarizen.taskContainer.model.Task;
import com.clarizen.taskContainer.model.TaskContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository("postgres")
public class TaskContainerPostgresDao implements TaskContainerDao {
    private final JdbcTemplate jdbcTemplate;

    Logger logger = LoggerFactory.getLogger(TaskContainerController.class);

    @Autowired
    public TaskContainerPostgresDao(JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;


        logger.info("creating level enum if not exits");
        jdbcTemplate.execute("" + "DO $$ BEGIN " +
                "CREATE TYPE level AS ENUM('NONE','HIGH','MEDIUM','LOW');" +
                "EXCEPTION " +
                "WHEN duplicate_object THEN null;" +
                "END $$;");

        logger.info("creating containers table if not exits");
        String sql = "CREATE TABLE IF NOT EXISTS" +
                " containers(" +
                " id UUID NOT NULL PRIMARY KEY, " +
                " name VARCHAR(250) NOT NULL)";
        jdbcTemplate.execute(sql);

        logger.info("creating tasks table if not exits");
        sql = "CREATE TABLE IF NOT EXISTS tasks" +
                "(" +
                "    task_id     UUID PRIMARY KEY NOT NULL," +
                "    task_name   VARCHAR(250)     NOT NULL," +
                "    description VARCHAR(250)," +
                "    due_date    DATE," +
                "    taskcontainer_id UUID NOT NULL," +
                "    priority level NOT NULL" +
                "        CHECK (" +
                "                priority = 'NONE' OR" +
                "                priority = 'HIGH' OR" +
                "                priority = 'MEDIUM' OR" +
                "                priority = 'LOW')" +
                ")";
        jdbcTemplate.execute(sql);
    }


    @Override
    public long createContainer(UUID id, TaskContainer container) {
        logger.info("inserting new container into containers DB table");
        String sql = "" +
                "INSERT INTO containers(" +
                " id, " +
                " name) " +
                "VALUES (?, ?)";
        return jdbcTemplate.update(
                sql,
                id,
                container.getName()
        );
    }

    @Override
    public UUID createTaskInContainer(UUID taskId, Task task, UUID containerID) throws DataNotFoundInDBException {
        if (!isContainerExists(containerID)) {
            throw new DataNotFoundInDBException("container does not exists in db");
        }

        logger.info("inserting new task into tasks DB table");
        String sql = "" +
                "INSERT INTO tasks(" +
                " task_id, task_name, description, due_date, taskcontainer_id, priority ) " +
                "VALUES (?, ?, ?, ?, ?, ?::level )  ";

        jdbcTemplate.update(
                sql,
                taskId,
                task.getName(),
                task.getDescription(),
                task.getDueDate(),
                containerID,
                task.getPriority().toString()
        );
        return taskId;
    }


    @Override
    public Optional<Task> getTaskByIds(UUID containerId, UUID taskId) throws DataNotFoundInDBException {
        if (!isContainerExists(containerId)) {
            throw new DataNotFoundInDBException("container does not exists in db");
        }
        logger.info("fetching task from DB table");
        final String sql = "SELECT task_id, task_name, description, due_date, taskcontainer_id, priority FROM tasks WHERE task_id = ?";
        Task task = jdbcTemplate.queryForObject(sql, new Object[]{taskId}, (resultSet, i) -> {
            String name = resultSet.getString("task_name");
            Date dueDate = resultSet.getDate("due_date");
            String priority = resultSet.getString("priority");
            String description = resultSet.getString("description");
            return new Task(taskId, name, dueDate, containerId, Task.Level.valueOf(priority), description);
        });

        return Optional.ofNullable(task);
    }

    @Override
    public List<Task> getAllTasksInContainers(UUID containerId) throws DataNotFoundInDBException {
        if (!isContainerExists(containerId)) {
            throw new DataNotFoundInDBException("container does not exists in db");
        }

        logger.info("fetching all tasks that belongs to container " + containerId + "from DB table and sorting them by priority");
        final String sql = "SELECT task_id, task_name, description, due_date, taskcontainer_id, priority FROM tasks WHERE taskcontainer_id = ?" +
                "ORDER BY array_position(ARRAY['NONE','HIGH','MEDIUM','LOW']::text[], priority);";

        return jdbcTemplate.query(sql, new Object[]{containerId}, (resultSet, i) -> {
            UUID taskId = UUID.fromString(resultSet.getString("task_id"));
            String name = resultSet.getString("task_name");
            Date dueDate = resultSet.getDate("due_date");
            String priority = resultSet.getString("priority");
            String description = resultSet.getString("description");
            return new Task(taskId, name, dueDate, containerId, Task.Level.valueOf(priority), description);
        });
    }

    @Override
    public long deleteContainer(UUID containerId) throws DataNotFoundInDBException {

        if (!isContainerExists(containerId)) {
            throw new DataNotFoundInDBException("container does not exists in db");
        }

        logger.info("deleting all tasks the belongs to container: " + containerId + " from db");
        String sql = "" +
                "DELETE FROM tasks " +
                "WHERE taskcontainer_id = ?";
        jdbcTemplate.update(sql, containerId);

        sql = "" +
                "DELETE FROM containers " +
                "WHERE id = ?";
        return jdbcTemplate.update(sql, containerId);


    }

    @Override
    public long deleteTaskInContainer(UUID containerId, UUID taskID) throws DataNotFoundInDBException {

        if (!isContainerExists(containerId)) {
            throw new DataNotFoundInDBException("container does not exists in db");
        }

        logger.info("deleting all tasks the belongs to container: " + containerId + " from db");
        String sql = "" +
                "DELETE FROM tasks " +
                "WHERE taskcontainer_id = ? AND task_id = ?";
        return jdbcTemplate.update(sql, containerId, taskID);
    }

    @Override
    public long updateTaskInContainer(UUID containerId, UUID taskId, Task taskToUpdate) throws DataNotFoundInDBException {
        if (!isContainerExists(containerId)) {
            throw new DataNotFoundInDBException("container does not exists in db");
        }

        logger.info("updating task " + taskId + " in db");
        String sql =
                "UPDATE tasks" +
                        " SET" +
                        " task_id = ?, task_name = ?, description = ?, due_date = ?, taskcontainer_id = ?, priority =?::level  " +
                        "WHERE taskcontainer_id = ? AND task_id = ?";
        return jdbcTemplate.update(
                sql,
                taskId,
                taskToUpdate.getName(),
                taskToUpdate.getDescription(),
                taskToUpdate.getDueDate(),
                containerId,
                taskToUpdate.getPriority().toString(),
                containerId,
                taskId
        );
    }

    @Override
    public long updateTaskPriority(UUID taskId, Task.Level priority) {
        logger.info("updating task " + taskId + " priority to :" + priority.toString() + " in DB");
        String sql = "" +
                "UPDATE tasks " +
                "SET priority = ?::level " +
                "WHERE task_id = ?";
        return jdbcTemplate.update(sql, priority.toString(), taskId);
    }


    private boolean isContainerExists(UUID containerId) {
        String sql = "SELECT count(*) FROM containers WHERE id = ?";
        boolean result = false;
        int count = jdbcTemplate.queryForObject(
                sql, new Object[]{containerId}, Integer.class);
        if (count > 0) {
            result = true;
        }
        return result;
    }

}

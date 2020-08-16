package com.clarizen.taskContainer.dao;

import com.clarizen.taskContainer.model.Task;
import com.clarizen.taskContainer.model.TaskContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository("postgres")
public class TaskContainerPostgresDao implements TaskContainerDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TaskContainerPostgresDao(JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;

        String sql = "CREATE TABLE IF NOT EXISTS" +
                " containers(" +
                " id UUID NOT NULL PRIMARY KEY, " +
                " name VARCHAR(250) NOT NULL)";
        jdbcTemplate.execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS tasks" +
                "(" +
                "    task_id     UUID PRIMARY KEY NOT NULL," +
                "    task_name   VARCHAR(250)     NOT NULL," +
                "    description VARCHAR(250)," +
                "    due_date    DATE," +
                "    taskcontainer_id UUID NOT NULL," +
                "    priority    TEXT NOT NULL" +
                "        CHECK (" +
                "                priority = 'NONE' OR" +
                "                priority = 'HIGH' OR" +
                "                priority = 'MEDIUM' OR" +
                "                priority = 'LOW')" +
                ")";
        jdbcTemplate.execute(sql);
    }


    @Override
    public int createContainer(UUID id, TaskContainer container) {
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
    public UUID createTaskInContainer(UUID taskId, Task task, UUID containerID) throws NullPointerException {
        if (!isContainerExists(containerID)) {
            throw new NullPointerException("container does not exists in db");
        }

        String sql = "" +
                "INSERT INTO tasks(" +
                " task_id, task_name, description, due_date, taskcontainer_id, priority ) " +
                "VALUES (?, ?, ?, ?, ?, ?)  ";

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
    public Optional<Task> getTaskByIds(UUID containerId, UUID taskId) {
        if (!isContainerExists(containerId)) {
            throw new NullPointerException("container does not exists in db");
        }
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
    public List<Task> getAllTasksInContainers(UUID containerId) {
        if (!isContainerExists(containerId)) {
            throw new NullPointerException("container does not exists in db");
        }

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
    public int deleteContainer(UUID containerId) {

        if (!isContainerExists(containerId)) {
            throw new NullPointerException("container does not exists in db");
        }

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
    public int deleteTaskInContainer(UUID containerId, UUID taskID) {

        if (!isContainerExists(containerId)) {
            throw new NullPointerException("container does not exists in db");
        }

        String sql = "" +
                "DELETE FROM tasks " +
                "WHERE taskcontainer_id = ? AND task_id = ?";
        return jdbcTemplate.update(sql, containerId, taskID);
    }

    @Override
    public int updateTaskInContainer(UUID containerId, UUID taskId, Task taskToUpdate) {
        if (!isContainerExists(containerId)) {
            throw new NullPointerException("container does not exists in db");
        }
        String sql =
                "UPDATE tasks" +
                        " SET" +
                        " task_id = ?, task_name = ?, description = ?, due_date = ?, taskcontainer_id = ?, priority =?  " +
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
    public int updateTaskPriority(UUID taskId, String priority) {
        String sql = "" +
                "UPDATE tasks " +
                "SET priority = ? " +
                "WHERE task_id = ?";
        return jdbcTemplate.update(sql, priority, taskId);
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

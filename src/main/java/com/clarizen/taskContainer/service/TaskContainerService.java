package com.clarizen.taskContainer.service;

import com.clarizen.taskContainer.dao.TaskContainerDao;
import com.clarizen.taskContainer.exceptions.DataNotFoundInDBException;
import com.clarizen.taskContainer.model.Task;
import com.clarizen.taskContainer.model.TaskContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskContainerService {

    private final TaskContainerDao taskContainerDao;

    @Autowired
    public TaskContainerService(@Qualifier("postgres") TaskContainerDao taskContainerDao) {
        this.taskContainerDao = taskContainerDao;
    }

    public long createContainer(TaskContainer container) {
        if (container.getId() == null) {
            return taskContainerDao.createContainer(container);
        } else return taskContainerDao.createContainer(container.getId(), container);
    }

    public UUID createTaskInContainer(Task task, UUID containerId) throws DataNotFoundInDBException {
        if (task.getId() == null) {
            return taskContainerDao.createTaskInContainer(task, containerId);
        } else return taskContainerDao.createTaskInContainer(task.getId(), task, containerId);
    }

    public Optional<Task> getTaskByIds(UUID containerId, UUID taskId) throws DataNotFoundInDBException {
        return taskContainerDao.getTaskByIds(containerId, taskId);
    }

    public List<Task> getAllTasksInContainers(UUID containerId) throws DataNotFoundInDBException {
        return taskContainerDao.getAllTasksInContainers(containerId);
    }

    public long deleteContainer(UUID id) throws DataNotFoundInDBException {
        return taskContainerDao.deleteContainer(id);
    }

    public long deleteTaskInContainer(UUID containerId, UUID taskId) throws DataNotFoundInDBException {
        return taskContainerDao.deleteTaskInContainer(containerId, taskId);
    }

    public long updateTaskInContainer(UUID containerId, UUID taskId, Task taskToUpdate) throws DataNotFoundInDBException {
        return taskContainerDao.updateTaskInContainer(containerId, taskId, taskToUpdate);
    }

    public long updateTaskPriority(UUID taskId, Task.Level priority) {
        return taskContainerDao.updateTaskPriority(taskId, priority);
    }
}

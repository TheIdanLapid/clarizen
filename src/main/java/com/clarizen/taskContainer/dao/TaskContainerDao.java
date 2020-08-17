package com.clarizen.taskContainer.dao;

import com.clarizen.taskContainer.exceptions.DataNotFoundInDBException;
import com.clarizen.taskContainer.model.Task;
import com.clarizen.taskContainer.model.TaskContainer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskContainerDao {

    long createContainer(UUID id, TaskContainer container);

    default long createContainer(TaskContainer container){
        UUID id = UUID.randomUUID();
        return  createContainer(id,container);
    }

    UUID createTaskInContainer(UUID taskId, Task task, UUID containerID) throws DataNotFoundInDBException;

    default UUID createTaskInContainer(Task task, UUID containerID) throws DataNotFoundInDBException {
        UUID taskId = UUID.randomUUID();
        return  createTaskInContainer(taskId, task ,containerID);
    }

    Optional<Task> getTaskByIds(UUID containerId, UUID taskId) throws DataNotFoundInDBException;

    List<Task> getAllTasksInContainers(UUID containerId) throws DataNotFoundInDBException;

    long deleteContainer(UUID containerId) throws DataNotFoundInDBException;

    long deleteTaskInContainer(UUID containerId , UUID taskID) throws DataNotFoundInDBException;

    long updateTaskInContainer(UUID containerId, UUID taskId, Task taskToUpdate) throws DataNotFoundInDBException;

    long updateTaskPriority(UUID taskId, Task.Level priority);


}

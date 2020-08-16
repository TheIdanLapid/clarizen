package com.clarizen.taskContainer.dao;

import com.clarizen.taskContainer.model.Task;
import com.clarizen.taskContainer.model.TaskContainer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskContainerDao {

    int createContainer(UUID id, TaskContainer container);

    default int createContainer(TaskContainer container){
        UUID id = UUID.randomUUID();
        return  createContainer(id,container);
    }

    UUID createTaskInContainer(UUID taskId, Task task, UUID containerID);

    default UUID createTaskInContainer(Task task, UUID containerID){
        UUID taskId = UUID.randomUUID();
        return  createTaskInContainer(taskId, task ,containerID);
    }

    Optional<Task> getTaskByIds(UUID containerId, UUID taskId);

    List<Task> getAllTasksInContainers(UUID containerId);

    int deleteContainer(UUID containerId);

    int deleteTaskInContainer(UUID containerId , UUID taskID);

    int updateTaskInContainer(UUID containerId, UUID taskId, Task taskToUpdate);

    int updateTaskPriority(UUID taskId, String priority);
}

package com.clarizen.taskContainer.service;

import com.clarizen.taskContainer.dao.TaskContainerDao;
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
    public TaskContainerService(@Qualifier("postgres") TaskContainerDao taskContainerDao){
        this.taskContainerDao = taskContainerDao;
    }

    public int createContainer(TaskContainer container){
        return taskContainerDao.createContainer(container);
    }

    public UUID createTaskInContainer(Task task, UUID containerId) {
        return taskContainerDao.createTaskInContainer(task, containerId);
    }

    public Optional<Task> getTaskByIds(UUID containerId, UUID taskId) {
        return taskContainerDao.getTaskByIds(containerId,taskId);
    }

    public List<Task> getAllTasksInContainers(UUID containerId) {
        return taskContainerDao.getAllTasksInContainers(containerId);
    }

    public int deleteContainer(UUID id){
        return taskContainerDao.deleteContainer(id);
    }

    public int deleteTaskInContainer(UUID containerId, UUID taskId) {
        return taskContainerDao.deleteTaskInContainer(containerId,taskId);
    }

    public int updateTaskInContainer(UUID containerId, UUID taskId, Task taskToUpdate) {
        return taskContainerDao.updateTaskInContainer(containerId,taskId,taskToUpdate);
    }

    public int updateTaskPriority(UUID taskId, String priority) {
        return taskContainerDao.updateTaskPriority(taskId,priority);
    }
}

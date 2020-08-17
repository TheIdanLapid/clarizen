package com.clarizen.taskContainer.controller;

import com.clarizen.taskContainer.exceptions.DataNotFoundInDBException;
import com.clarizen.taskContainer.model.Task;
import com.clarizen.taskContainer.model.TaskContainer;
import com.clarizen.taskContainer.service.TaskContainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.metrics.annotation.Timed;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Timed
@RequestMapping("api/v1/")
@RestController
public class TaskContainerController {
    private final TaskContainerService taskContainerService;
    Logger logger = LoggerFactory.getLogger(TaskContainerController.class);

    @Autowired
    public TaskContainerController(TaskContainerService taskContainerService) {
        this.taskContainerService = taskContainerService;

    }

    @PostMapping
    public void createContainer(@Valid @NotNull @RequestBody TaskContainer container, Errors errors) throws IllegalArgumentException {
        logger.info("controller received - create container request");

        if (errors.hasErrors()) {
            throw new IllegalArgumentException(errors.getFieldError().getDefaultMessage());
        }
        taskContainerService.createContainer(container);
    }

    @PostMapping(path = "{containerId}/tasks")
    public UUID createTaskInContainer(@Valid @RequestBody Task task, @PathVariable("containerId") UUID containerId) throws DataNotFoundInDBException {
        logger.info("controller received - create task in container " + containerId + " request");
        return taskContainerService.createTaskInContainer(task, containerId);
    }

    @GetMapping(path = "{containerId}/tasks/{taskId}")
    public Task getTaskByIds(@PathVariable("containerId") UUID containerId, @PathVariable("taskId") UUID taskId) throws DataNotFoundInDBException {
        logger.info("controller received - get task " + taskId + " by id request");
        return taskContainerService.getTaskByIds(containerId, taskId).orElse(null);
    }

    @GetMapping(path = "{containerId}/tasks/")
    public List<Task> getAllTasksInContainer(@PathVariable("containerId") UUID containerId) throws DataNotFoundInDBException {
        logger.info("controller received - get all tasks in container " + containerId + " request");
        return taskContainerService.getAllTasksInContainers(containerId);
    }

    @DeleteMapping(path = "{containerId}")
    public void deleteContainer(@PathVariable("containerId") UUID containerId) throws DataNotFoundInDBException {
        logger.info("controller received - delete container " + containerId + " request");
        taskContainerService.deleteContainer(containerId);
    }

    @DeleteMapping(path = "{containerId}/tasks/{taskId}")
    public void deleteTaskInContainer(@PathVariable("containerId") UUID containerId, @PathVariable("taskId") UUID taskId) throws DataNotFoundInDBException {
        logger.info("controller received - delete task +" + taskId + "in container  " + containerId + " request");
        taskContainerService.deleteTaskInContainer(containerId, taskId);
    }


    @PutMapping(path = "{containerId}/tasks/{taskId}")
    public void updateTaskInContainer(@PathVariable("containerId") UUID containerId, @PathVariable("taskId") UUID taskId, @Valid @NotNull @RequestBody Task taskToUpdate) throws DataNotFoundInDBException {
        logger.info("controller received - update task +" + taskId + "in container  " + containerId + " request");
        taskContainerService.updateTaskInContainer(containerId, taskId, taskToUpdate);
    }

    @PutMapping(path = "{taskId}/priority")
    public void updateTaskPriority(@PathVariable("taskId") UUID taskId, @NotNull @RequestBody String priority) {
        logger.info("controller received - update task +" + taskId + "priority to  " + priority + " request");
        taskContainerService.updateTaskPriority(taskId, Task.Level.valueOf(priority));
    }


}



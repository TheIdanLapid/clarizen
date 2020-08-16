package com.clarizen.taskContainer.api;

import com.clarizen.taskContainer.model.Task;
import com.clarizen.taskContainer.model.TaskContainer;
import com.clarizen.taskContainer.service.TaskContainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;


@RequestMapping("api/v1/")
@RestController
public class TaskContainerController {
    private final TaskContainerService taskContainerService;

    @Autowired
    public TaskContainerController(TaskContainerService taskContainerService) {
        this.taskContainerService = taskContainerService;
    }

    @PostMapping
    public void createContainer(@Valid @NotNull @RequestBody TaskContainer container, Errors errors) throws IllegalArgumentException {
        if (errors.hasErrors()) {
            throw new IllegalArgumentException(errors.getFieldError().getDefaultMessage());
        }
        taskContainerService.createContainer(container);
    }

    @PostMapping(path = "{containerId}/tasks")
    public UUID createTaskInContainer(@Valid @RequestBody Task task, @PathVariable("containerId") UUID containerId) {
        return taskContainerService.createTaskInContainer(task, containerId);
    }

    @GetMapping(path = "{containerId}/tasks/{taskId}")
    public Task getTaskByIds(@PathVariable("containerId") UUID containerId, @PathVariable("taskId") UUID taskId) {
        return taskContainerService.getTaskByIds(containerId, taskId).orElse(null);
    }

    @GetMapping(path = "{containerId}/tasks/")
    public List<Task> getAllTasksInContainer(@PathVariable("containerId") UUID containerId) {
        return taskContainerService.getAllTasksInContainers(containerId);
    }

    @DeleteMapping(path = "{containerId}")
    public void deleteContainer(@PathVariable("containerId") UUID containerId) {
        taskContainerService.deleteContainer(containerId);
    }

    @DeleteMapping(path = "{containerId}/tasks/{taskId}")
    public void deleteTaskInContainer(@PathVariable("containerId") UUID containerId, @PathVariable("taskId") UUID taskId) {
        taskContainerService.deleteTaskInContainer(containerId, taskId);
    }


    @PutMapping(path = "{containerId}/tasks/{taskId}")
    public void updateTaskInContainer(@PathVariable("containerId") UUID containerId, @PathVariable("taskId") UUID taskId, @Valid @NotNull @RequestBody Task taskToUpdate) {
        taskContainerService.updateTaskInContainer(containerId, taskId, taskToUpdate);
    }

    @PutMapping(path = "{taskId}/priority")
    public void updateTaskPriority(@PathVariable("taskId") UUID taskId, @Valid @NotNull @RequestBody String priority) {
        taskContainerService.updateTaskPriority(taskId, priority);
    }


}



package com.clarizen.taskContainer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class Task {

    private final UUID id;

    @NotBlank @Size(max = 250)
    private final String name;

    private final Date dueDate;

    private final UUID taskContainerId;

    public enum Level {
        LOW,
        MEDIUM,
        HIGH,
        NONE
    }

    private final Level priority;



    @Size(max = 250)
    private final String description;

    public Task(@JsonProperty("id") UUID id,
                @JsonProperty("name") String name,
                @JsonProperty("dueDate") Date dueDate,
                @JsonProperty("taskContainerId") UUID taskContainerId,
                @JsonProperty("priority") Level priority,
                @JsonProperty("description") String description) {
        this.id = id;
        this.name = name;
        this.dueDate = dueDate;
        this.taskContainerId = taskContainerId;
        this.priority = Objects.requireNonNullElse(priority, Level.NONE);
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public UUID getTaskContainerId() {
        return taskContainerId;
    }

    public Level getPriority() {
        return priority;
    }



    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dueDate=" + dueDate +
                ", taskContainerId=" + taskContainerId +
                ", priority=" + priority +
                ", description='" + description + '\'' +
                '}';
    }

}

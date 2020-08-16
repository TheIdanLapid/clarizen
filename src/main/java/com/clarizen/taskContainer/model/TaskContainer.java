package com.clarizen.taskContainer.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.UUID;

public class TaskContainer {
    private final UUID id;

    @NotBlank
    @Size(max = 250)
    private final String name;



    public TaskContainer(@JsonProperty("id") UUID id,
                         @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

package com.clarizen.TaskContainer;

import com.clarizen.taskContainer.api.TaskContainerController;
import com.clarizen.taskContainer.model.TaskContainer;
import com.clarizen.taskContainer.service.TaskContainerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.awt.*;
import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class ContainerTests {

    @RunWith(SpringRunner.class)
    @WebMvcTest(TaskContainerController.class)
    public class EmployeeRestControllerIntegrationTest {

        @Autowired
        private MockMvc mvc;

        @MockBean
        private TaskContainerService service;

        @Test
        public void givenEmployees_whenGetEmployees_thenReturnJsonArray()
                throws Exception {

            UUID id = UUID.randomUUID();
            TaskContainer alex = new TaskContainer(id,"HI");


            given(service.createContainer(alex)).willReturn(null);

            mvc.perform(get("/api/v1/" + id)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        // write test cases here
    }

}

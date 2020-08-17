package com.clarizen.TaskContainer;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TaskContainerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;
    private JSONObject jsonRequestBody;

    private UUID containerId;
    private UUID taskId;
    private String url = "http://localhost:8080/api/v1/";


    Logger logger = LoggerFactory.getLogger(TaskContainerTest.class);

    @Before
    public void setUp() throws JSONException {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        containerId = UUID.randomUUID();
        taskId = UUID.randomUUID();
    }


    @Test
    public void ValidNormalFlow() throws Exception {
        String request;

        CreateContainerAndTask();

        logger.info("--testing update task in task container --");
        jsonRequestBody = new JSONObject().put("dueDate", "2020-02-15T00:00:00Z").put("name", "newName");
        request = url + containerId + "/tasks/" + taskId;
        mockMvc.perform(put(request).contentType(MediaType.APPLICATION_JSON).content(jsonRequestBody.toString()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

        logger.info("--testing update task priority --");
        request = url + taskId + "/priority";
        mockMvc.perform(put(request).contentType(MediaType.TEXT_PLAIN).content("HIGH").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());


        logger.info("--testing get task in task container --");
        request = url + containerId + "/tasks/" + taskId;
        mockMvc.perform(get(request).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

        logger.info("--testing delete task in task container --");
        request = url + containerId + "/tasks/" + taskId;
        mockMvc.perform(delete(request).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

        logger.info("--testing delete task container --");
        request = url + containerId;
        mockMvc.perform(delete(request).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

    }

    private void CreateContainerAndTask() throws Exception {
        String request;
        logger.info("--testing create task container --");
        jsonRequestBody = new JSONObject().put("id", containerId).put("name", "megaContainer");
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(jsonRequestBody.toString()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

        logger.info("--testing create task in task container --");
        jsonRequestBody = new JSONObject().put("id", taskId).put("name", "firstTask");
        request = url + containerId + "/tasks";
        mockMvc.perform(post(request).contentType(MediaType.APPLICATION_JSON).content(jsonRequestBody.toString()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    public void AddTaskToNonexistentContainer() throws Exception {
        String request = "http://localhost:8080/api/v1/e94a5254-8738-4ed1-89be-c4cc9d140400/tasks/";
        mockMvc.perform(get(request)).andExpect(status().isNotFound()).andExpect(jsonPath("$.message").value("requested data was not found in database: container does not exists in db"));
    }

    @Test
    public void PutBadPriority() throws Exception {
        CreateContainerAndTask();
        String request = "http://localhost:8080/api/v1/" + taskId +"/priority/";
        mockMvc.perform(put(request).contentType(MediaType.TEXT_PLAIN).content("SO SO").accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }




}

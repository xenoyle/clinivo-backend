package edu.uscb.csci470sp26.clinivo_backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(UserControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private Long testUserId;

    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();

        String newUserJson = """
        {
          "firstName": "John",
          "lastName": "Doe",
          "email": "john@test.com",
          "password": "123",
          "role": "PATIENT"
        }
        """;

        String response = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newUserJson))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Integer id = JsonPath.read(response, "$.id");
        testUserId = id.longValue();

        logger.info("Setup complete. Test user ID: {}", testUserId);
    }

    @Test
    public void testGetUserById() throws Exception {

        mockMvc.perform(get("/api/users/{id}", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUserId));
    }

    @Test
    public void testCreateUser() throws Exception {

        String newUserJson = """
        {
          "firstName": "Jane",
          "lastName": "Doe",
          "email": "jane@test.com",
          "password": "123",
          "role": "PROVIDER"
        }
        """;

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newUserJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("jane@test.com"));
    }

    @Test
    public void testGetAllUsers() throws Exception {

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testUpdateUser() throws Exception {

        String updatedUserJson = """
        {
          "firstName": "Updated",
          "lastName": "User",
          "email": "updated@test.com",
          "role": "PROVIDER"
        }
        """;

        mockMvc.perform(put("/api/users/{id}", testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedUserJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@test.com"));
    }

    @Test
    public void testDeleteUser() throws Exception {

        mockMvc.perform(delete("/api/users/{id}", testUserId))
                .andExpect(status().isOk());
    }
}

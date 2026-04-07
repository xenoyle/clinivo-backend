package edu.uscb.csci470sp26.clinivo_backend.controller;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class ConversationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private Long user1Id;
    private Long user2Id;

    @BeforeEach
    void setup() throws Exception {

        String userJson = """
        {
          "firstName": "Test",
          "lastName": "User",
          "email": "user1@test.com",
          "password": "123",
          "role": "PATIENT"
        }
        """;

        String response1 = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andReturn().getResponse().getContentAsString();

        user1Id = JsonPath.read(response1, "$.id");

        String response2 = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson.replace("user1", "user2")))
                .andReturn().getResponse().getContentAsString();

        user2Id = JsonPath.read(response2, "$.id");
    }

    @Test
    void testCreateConversation() throws Exception {

        String json = "[" + user1Id + "," + user2Id + "]";

        mockMvc.perform(post("/api/conversations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }
}

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

import jakarta.transaction.Transactional;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private Long user1Id;
    private Long user2Id;
    private Long conversationId;

    @BeforeEach
    void setup() throws Exception {

    	long timestamp = System.currentTimeMillis();

    	String doctorJson = """
    	{
    	  "firstName": "Test",
    	  "lastName": "User",
    	  "email": "doctor_%d@test.com",
    	  "password": "123",
    	  "phoneNumber": "1234567890",
    	  "role": "DOCTOR"
    	}
    	""".formatted(timestamp);

    	String patientJson = """
    	{
    	  "firstName": "Test",
    	  "lastName": "User",
    	  "email": "patient_%d@test.com",
    	  "password": "123",
    	  "phoneNumber": "1234567890",
    	  "role": "PATIENT"
    	}
    	""".formatted(timestamp);

        // Create user 1
        String response1 = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(doctorJson))
                .andReturn().getResponse().getContentAsString();

        Integer id1 = JsonPath.read(response1, "$.id");
        user1Id = id1.longValue();

        // Create user 2
        String response2 = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(patientJson.replace("test1", "test2")))
                .andReturn().getResponse().getContentAsString();

        Integer id2 = JsonPath.read(response2, "$.id");
        user2Id = id2.longValue();

        // Create conversation
        String conversationJson = "[" + user1Id + "," + user2Id + "]";

        String convoResponse = mockMvc.perform(post("/api/conversations/" + user1Id + "/" + user2Id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(conversationJson))
                .andReturn().getResponse().getContentAsString();

        Integer convoId = JsonPath.read(convoResponse, "$.id");
        conversationId = convoId.longValue();
    }

    @Test
    void testSendMessage() throws Exception {

        String messageJson = """
        {
          "conversationId": %d,
          "senderId": %d,
          "content": "Hello!"
        }
        """.formatted(conversationId, user1Id);

        mockMvc.perform(post("/api/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(messageJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Hello!"));
    }

    @Test
    void testGetMessages() throws Exception {

        testSendMessage();

        mockMvc.perform(get("/api/messages/conversation/{id}", conversationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Hello!"));
    }
}

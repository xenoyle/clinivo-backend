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
public class ConversationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private Long user1Id;
    private Long user2Id;

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

        String response1 = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(doctorJson))
                .andReturn().getResponse().getContentAsString();

        user1Id = ((Number)JsonPath.read(response1, "$.id")).longValue();

        String response2 = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(patientJson.replace("user1", "user2")))
                .andReturn().getResponse().getContentAsString();

        user2Id = ((Number)JsonPath.read(response2, "$.id")).longValue();
    }

    @Test
    void testCreateConversation() throws Exception {

        String json = "[" + user1Id + "," + user2Id + "]";

        mockMvc.perform(post("/api/conversations/" + user1Id + "/" + user2Id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }
}

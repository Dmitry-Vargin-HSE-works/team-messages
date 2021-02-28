package com.giggle.team.controller;

import com.giggle.team.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import org.junit.jupiter.api.Test;

// TODO deal with 401
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    void postUser() throws Exception {

    }

    @Test
    void getUserExample() throws Exception {
        /*
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/example")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        Assertions.assertEquals(result.getResponse().getStatus(), 200);
         */
    }

    @Test
    void getUserByUsername() throws Exception {
        /*
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                .content(new ObjectMapper().writeValueAsString("testuser"))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
        Assertions.assertEquals(result.getResponse().getStatus(), 404);

        MvcResult result2 = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                .content(new ObjectMapper().writeValueAsString("admin"))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        Assertions.assertEquals(result.getResponse().getStatus(), 200);
         */
    }
}

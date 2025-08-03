package com.webbee.audit_lib.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webbee.audit_lib.model.LoginRequest;
import com.webbee.audit_lib.util.TestApp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestApp.class)
@AutoConfigureMockMvc
class HttpLoggingFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testHttpLogging() throws Exception {
        LoginRequest request = new LoginRequest("Serega", "123");
        mockMvc.perform(post("/auth/signin")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request))
                        .param("token", "testToken")
                        .param("testParam", "test")
                        .param("param3", "test")
                        .param("param4", "test"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").exists());
    }

}
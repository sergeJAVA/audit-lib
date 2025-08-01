package com.webbee.audit_lib.interceptor;

import com.webbee.audit_lib.util.TestApp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest(classes = TestApp.class)
public class OutgoingHttpLoggingInterceptorTest {

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void testInterceptorLogsSuccessfulRequest() {
        String url = "http://test-api.com/users";
        String requestBody = "{\"id\":1, \"name\":\"test\"}";
        String responseBody = "{\"status\":\"success\"}";

        mockServer.expect(once(), requestTo(url))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        restTemplate.postForEntity(url, requestBody, String.class);
    }

}

package com.webbee.audit_lib.service;

import com.webbee.audit_lib.util.TestApp;
import com.webbee.audit_lib.util.TestService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("test")
public class TestServiceTest {

    @Autowired
    private TestService testService;

    @Test
    void method() {
        assertEquals("Hello 32", testService.method("Hello", 32));
    }

    @Test
    void exception() {
        assertThrows(RuntimeException.class, () -> testService.testException("Test"));
    }

}

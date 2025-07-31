package com.webbee.audit_lib.util;

import com.webbee.audit_lib.annotation.AuditLog;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    @AuditLog
    public String method(String word, int num) {
        return word + " " + num;
    }

    @AuditLog
    public String testException(String text) {
        throw new RuntimeException("Test exception");
    }

}

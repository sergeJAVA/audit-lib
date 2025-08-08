package com.webbee.audit_lib.util;

import com.webbee.audit_lib.model.LoginRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @PostMapping("/auth/signin")
    public AuthResponse signIn(@RequestBody LoginRequest request) {
        return new AuthResponse("Пользователь с логином " + request.getUsername() + " и с паролем " + request.getPassword() + " зарегистрирован");
    }

    static class AuthResponse {
        private String state;

        public AuthResponse(String state) {
            this.state = state;
        }

        public String getState() {
            return state;
        }

    }

}

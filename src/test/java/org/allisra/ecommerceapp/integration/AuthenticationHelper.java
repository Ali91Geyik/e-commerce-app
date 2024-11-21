package org.allisra.ecommerceapp.integration;


import org.allisra.ecommerceapp.model.dto.auth.LoginRequest;
import org.allisra.ecommerceapp.model.dto.auth.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
   public class AuthenticationHelper {

        @Autowired
        private TestRestTemplate restTemplate;

        public String getAuthToken(String email, String password) {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail(email);
            loginRequest.setPassword(password);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<LoginRequest> request = new HttpEntity<>(loginRequest, headers);

            ResponseEntity<LoginResponse> response = restTemplate.postForEntity(
                    "/api/v1/auth/login",
                    request,
                    LoginResponse.class
            );

            if (response.getBody() != null) {
                return response.getBody().getToken();
            }
            throw new RuntimeException("Authentication failed");
        }
    }


package com.codecool.solarwatch.integration;

import com.codecool.solarwatch.dto.auth.AuthResponse;
import com.codecool.solarwatch.dto.auth.RegisterRequest;
import com.codecool.solarwatch.model.User;
import com.codecool.solarwatch.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RegistrationIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Test
    void register_createsUserInDatabase_andReturnsJwtToken() {
        RegisterRequest request = new RegisterRequest("newuser", "secretPassword123");

        ResponseEntity<AuthResponse> response =
                restTemplate.postForEntity("/api/auth/register", request, AuthResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().token());
        assertFalse(response.getBody().token().isBlank());

        Optional<User> savedUser = userRepository.findByUsername("newuser");
        assertTrue(savedUser.isPresent());
        assertEquals("newuser", savedUser.get().getUsername());
        assertNotEquals("secretPassword123", savedUser.get().getPassword());
    }
}
package com.codecool.solarwatch.controller;

import com.codecool.solarwatch.dto.auth.AuthResponse;
import com.codecool.solarwatch.dto.auth.LoginRequest;
import com.codecool.solarwatch.dto.auth.RegisterRequest;
import com.codecool.solarwatch.model.Role;
import com.codecool.solarwatch.model.User;
import com.codecool.solarwatch.repository.UserRepository;
import com.codecool.solarwatch.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        User user = new User(
                request.username(),
                passwordEncoder.encode(request.password()),
                Role.USER
        );
        userRepository.save(user);

        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        User user = userRepository.findByUsername(request.username())
                .orElseThrow();

        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }
}
package com.api.student_management.controllers;


import com.api.student_management.dtos.LoginResponse;
import com.api.student_management.dtos.LoginUserDto;
import com.api.student_management.dtos.RegisterUserDto;
import com.api.student_management.entities.User;
import com.api.student_management.services.AuthenticationService;
import com.api.student_management.services.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;
    private static final Logger logger = (Logger) LoggerFactory.getLogger(AuthenticationController.class);


    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        logger.info("User trying to register: {}", registerUserDto);
        User registeredUser = authenticationService.signup(registerUserDto);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        logger.info("User trying to login: {}", loginUserDto);
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        logger.info("Authenticated User to login: {}", authenticatedUser);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        logger.info("User trying to login: {}", loginUserDto);
        logger.info("Generated JWT Token: {}", jwtToken);
        logger.info("User trying to login: {}", authenticatedUser);
        LoginResponse loginResponse = new LoginResponse().setToken(jwtToken).setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }
}
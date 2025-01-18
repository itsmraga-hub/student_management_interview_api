package com.api.student_management.services;

import com.api.student_management.dtos.LoginUserDto;
import com.api.student_management.dtos.RegisterUserDto;
import com.api.student_management.entities.User;
import com.api.student_management.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private static final Logger logger = (Logger) LoggerFactory.getLogger(AuthenticationService.class);


    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(RegisterUserDto input) {
        User user = new User();
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setFullName(input.getFullName());
        return userRepository.save(user);
    }

    public User authenticate(LoginUserDto input) {

//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        input.getEmail(),
//                        input.getPassword()
//                )
//        );


//        logger.info("User authenticated 222: {}", input.getEmail());
        return userRepository.findByEmail(input.getEmail())
                .orElseThrow();
    }
}
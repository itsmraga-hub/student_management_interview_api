package com.api.student_management.services;

import com.api.student_management.dtos.LoginUserDto;
import com.api.student_management.dtos.RegisterUserDto;
import com.api.student_management.entities.User;
import com.api.student_management.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private static final Logger logger = (Logger) LoggerFactory.getLogger(AuthenticationService.class);


    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            BCryptPasswordEncoder passwordEncoder
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

        User user = userRepository.findByEmail(input.getEmail())
                .orElseThrow();

        var pass = passwordEncoder.encode(input.getPassword());
//        logger.info("Password: {}",pass);
//        logger.info("User password: {}",user.getPassword());

        if (!passwordEncoder.matches(input.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        input.getEmail(),
//                        input.getPassword()
//                )
//        );


//        logger.info("User authenticated 222: {}", input.getEmail());
        return user;
    }
}
package com.api.student_management.initializer;

import com.api.student_management.entities.User;
import com.api.student_management.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

@Component
public class DefaultUserInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        createDefaultUser("John Doe", "john.doe@example.com", "password123");
        createDefaultUser("Jane Smith", "jane.smith@example.com", "password456");
        createDefaultUser("William Raga", "raga@example.com", "password456");
        createDefaultUser("Lynne", "lynn@example.com", "password456");
    }

    private void createDefaultUser(String fullName, String email, String rawPassword) {
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isEmpty()) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            User user = new User();
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(rawPassword));
            userRepository.save(user);
            System.out.println("Default user created: " + email);
        }
    }
}

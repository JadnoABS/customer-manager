package com.jadno.datum.ClientManager.config;

import com.jadno.datum.ClientManager.db.user.User;
import com.jadno.datum.ClientManager.db.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Bean
    public CommandLineRunner initUsers(UserRepository repository, PasswordEncoder encoder) {
        return args -> {
            if (userRepository.findByUsername("ADMIN").isEmpty()) {
                userRepository.save(User.builder()
                    .username("ADMIN")
                    .password(encoder.encode("admin123"))
                    .enabled(true)
                    .roles(Set.of("ADMIN", "USER"))
                    .build());
            }
            if (userRepository.findByUsername("USER").isEmpty()) {
                userRepository.save(User.builder()
                    .username("USER")
                    .password(encoder.encode("user123"))
                    .enabled(true)
                    .roles(Set.of("USER"))
                    .build());
            }
        };
    }
}

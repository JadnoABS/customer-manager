package com.jadno.datum.CustomerManager.config;

import com.jadno.datum.CustomerManager.db.profile.Profile;
import com.jadno.datum.CustomerManager.db.profile.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataInitializer {

    @Autowired
    private ProfileRepository profileRepository;

    @Value("${profiles.admin.password}")
    private String adminPassword;

    @Value("${profiles.user.password}")
    private String userPassword;

    @Bean
    public CommandLineRunner initUsers(ProfileRepository repository, PasswordEncoder encoder) {
        return args -> {
            if (profileRepository.findByUsername("ADMIN").isEmpty()) {
                profileRepository.save(Profile.builder()
                    .username("ADMIN")
                    .password(encoder.encode(adminPassword))
                    .enabled(true)
                    .roles(Set.of("ADMIN", "USER"))
                    .build());
            }
            if (profileRepository.findByUsername("USER").isEmpty()) {
                profileRepository.save(Profile.builder()
                    .username("USER")
                    .password(encoder.encode(userPassword))
                    .enabled(true)
                    .roles(Set.of("USER"))
                    .build());
            }
        };
    }
}

package com.jadno.datum.CustomerManager.db.profile;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import(ProfileRepositoryTest.CacheConfig.class)
class ProfileRepositoryTest {

    @Autowired
    private ProfileRepository profileRepository;

    @Test
    @DisplayName("Should persist and find a profile with all roles")
    void shouldFindProfileByUsername() {
        profileRepository.saveAndFlush(profile("ADMIN", Set.of("ADMIN", "USER")));

        Profile found = profileRepository.findByUsername("ADMIN").orElseThrow();

        assertTrue(found.isEnabled());
        assertEquals(Set.of("ADMIN", "USER"), found.getRoles());
        assertTrue(profileRepository.findByUsername("MISSING").isEmpty());
    }

    @Test
    @DisplayName("Should enforce unique usernames")
    void shouldRejectDuplicatedUsername() {
        profileRepository.saveAndFlush(profile("USER", Set.of("USER")));

        assertThrows(
                DataIntegrityViolationException.class,
                () -> profileRepository.saveAndFlush(profile("USER", Set.of("ADMIN")))
        );
    }

    private Profile profile(String username, Set<String> roles) {
        return Profile.builder()
                .username(username)
                .password("encoded-password")
                .enabled(true)
                .roles(roles)
                .build();
    }

    @TestConfiguration
    static class CacheConfig {

        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager();
        }
    }
}

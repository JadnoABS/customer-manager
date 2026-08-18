package com.jadno.datum.CustomerManager.config;

import com.jadno.datum.CustomerManager.api.CustomerController;
import com.jadno.datum.CustomerManager.db.profile.Profile;
import com.jadno.datum.CustomerManager.db.profile.ProfileRepository;
import com.jadno.datum.CustomerManager.domain.CustomerService;
import com.jadno.datum.CustomerManager.domain.ScoreService;
import com.jadno.datum.CustomerManager.dto.CustomerResponseDTO;
import com.jadno.datum.CustomerManager.dto.Status;
import com.jadno.datum.CustomerManager.security.CustomUserDetailsService;
import jakarta.servlet.Filter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitConfig(classes = SecurityConfigTest.TestConfig.class)
@WebAppConfiguration
class SecurityConfigTest {

    private static final String VALID_CUSTOMER = """
            {
              "name": "Cliente Datum",
              "cpf": "63276284006",
              "email": "cliente@datum.com",
              "status": "ACTIVE"
            }
            """;

    @Autowired
    private Filter springSecurityFilterChain;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        CustomerService customerService = mock(CustomerService.class);
        ScoreService scoreService = mock(ScoreService.class);
        CustomerController controller = new CustomerController();
        ReflectionTestUtils.setField(controller, "customerService", customerService);
        ReflectionTestUtils.setField(controller, "scoreService", scoreService);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .apply(springSecurity(springSecurityFilterChain))
                .build();

        when(profileRepository.findByUsername("USER")).thenReturn(Optional.of(profile("USER", "user123", Set.of("USER"))));
        when(profileRepository.findByUsername("ADMIN")).thenReturn(Optional.of(profile("ADMIN", "admin123", Set.of("ADMIN", "USER"))));
        when(customerService.getWithId(1L)).thenReturn(
                new CustomerResponseDTO(1L, "Cliente Datum", "63276284006", "cliente@datum.com", Status.ACTIVE)
        );
        when(customerService.create(any())).thenReturn(
                new CustomerResponseDTO(1L, "Cliente Datum", "63276284006", "cliente@datum.com", Status.ACTIVE)
        );
    }

    @Test
    @DisplayName("Should reject unauthenticated requests")
    void shouldReturn401WithoutCredentials() throws Exception {
        mockMvc.perform(get("/customer/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should allow USER to query customers")
    void shouldAllowUserToRead() throws Exception {
        mockMvc.perform(get("/customer/1").with(httpBasic("USER", "user123")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should reject invalid Basic Authentication credentials")
    void shouldRejectInvalidCredentials() throws Exception {
        mockMvc.perform(get("/customer/1").with(httpBasic("USER", "wrong-password")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should forbid USER from creating customers")
    void shouldForbidUserFromWriting() throws Exception {
        mockMvc.perform(post("/customer")
                        .with(httpBasic("USER", "user123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CUSTOMER))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should allow ADMIN to create customers")
    void shouldAllowAdminToWrite() throws Exception {
        mockMvc.perform(post("/customer")
                        .with(httpBasic("ADMIN", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CUSTOMER))
                .andExpect(status().isCreated());
    }

    private Profile profile(String username, String password, Set<String> roles) {
        return Profile.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .enabled(true)
                .roles(roles)
                .build();
    }

    @Configuration
    @EnableWebMvc
    @Import({SecurityConfig.class, CustomUserDetailsService.class})
    static class TestConfig {

        @Bean
        ProfileRepository profileRepository() {
            return mock(ProfileRepository.class);
        }
    }
}

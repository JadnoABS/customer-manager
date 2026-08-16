package com.jadno.datum.CustomerManager.security;

import com.jadno.datum.CustomerManager.db.profile.Profile;
import com.jadno.datum.CustomerManager.db.profile.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private ProfileRepository profileRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Profile profile = profileRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Profile " + username + " not found!"));

        List<SimpleGrantedAuthority> authorities = profile.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();

        return org.springframework.security.core.userdetails.User.builder()
                .username(profile.getUsername())
                .password(profile.getPassword())
                .disabled(!profile.isEnabled())
                .authorities(authorities)
                .build();
    }
}

package com.jadno.datum.CustomerManager.db.profile;

import java.util.HashSet;
import java.util.Set;

public class ProfileBuilder {

    private String username;
    private String password;
    private boolean enabled = true;
    private Set<String> roles = new HashSet<>();

    public ProfileBuilder username(String username) {
        this.username = username;
        return this;
    }

    public ProfileBuilder password(String password) {
        this.password = password;
        return this;
    }

    public ProfileBuilder enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public ProfileBuilder roles(Set<String> roles) {
        this.roles = roles;
        return this;
    }

    public Profile build() {
        return new Profile(username, password, enabled, roles);
    }
}
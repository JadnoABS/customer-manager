package com.jadno.datum.ClientManager.db.user;

import java.util.HashSet;
import java.util.Set;

public class UserBuilder {

    private String username;
    private String password;
    private boolean enabled = true;
    private Set<String> roles = new HashSet<>();

    public UserBuilder username(String username) {
        this.username = username;
        return this;
    }

    public UserBuilder password(String password) {
        this.password = password;
        return this;
    }

    public UserBuilder enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public UserBuilder roles(Set<String> roles) {
        this.roles = roles;
        return this;
    }

    public User build() {
        return new User(username, password, enabled, roles);
    }
}
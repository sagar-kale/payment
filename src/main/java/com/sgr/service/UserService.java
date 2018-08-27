package com.sgr.service;

import com.sgr.domain.User;

public interface UserService {
    void save(User user);

    User findByUsername(String username);
}

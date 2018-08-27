package com.sgr.service;

import com.sgr.domain.User;
import com.sgr.repositories.jpa.JpaUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private JpaUserRepository jpaUserRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void save(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        jpaUserRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return jpaUserRepository.findByUsername(username);
    }
}

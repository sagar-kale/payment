package com.sgr.service;

import com.sgr.controller.UserController;
import com.sgr.domain.Role;
import com.sgr.domain.User;
import com.sgr.repositories.jpa.JpaUserRepository;
import com.sgr.repositories.jpa.UserRoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private JpaUserRepository jpaUserRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void save(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        Role role = new Role();
        role.setRole("user");
        if (user.getFirstName().equalsIgnoreCase("sagar"))
            role.setRole("admin");
        HashSet<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        HashSet<User> users = new HashSet<>();
        users.add(user);
        role.setUsers(users);

        logger.debug("Authentication Role for user ::: " + user.getUsername());
        logger.debug("Roles ::: " + user.getRoles());
        userRoleRepository.save(role);
        jpaUserRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return jpaUserRepository.findByUsername(username);
    }

    @Override
    public User findUserByEmail(String email) {
        return jpaUserRepository.findByEmail(email);
    }

    @Override
    public void createUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        //user.setActive(true);
        Role userRole = userRoleRepository.findByRole("admin");
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
        jpaUserRepository.save(user);
    }

    @Override
    public void saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
//        user.setActive(true);
        Role userRole = userRoleRepository.findByRole("admin");
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
        jpaUserRepository.save(user);
    }

    @Override
    public List<User> findAll() {
        return jpaUserRepository.findAll();
    }

    public List<Role> findAllRoles() {
        return userRoleRepository.findAll();
    }


}

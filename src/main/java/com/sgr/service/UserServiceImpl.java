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
        Role role_user = new Role();
        role_user.setRole("user");
        role_user.setUsers(new HashSet<User>() {{
            add(user);
        }});
        Role role_admin = new Role();
        role_admin.setRole("admin");
        role_admin.setUsers(new HashSet<User>() {{
            add(user);
        }});
        if (user.getFirstName().equalsIgnoreCase("sagar")) {
            user.setRoles(new HashSet<Role>() {{
                add(role_user);
                add(role_admin);
            }});
        } else {
            user.setRoles(new HashSet<Role>() {{
                add(role_user);
            }});
        }
        User save = jpaUserRepository.save(user);
        for (Role role : save.getRoles()) {
            userRoleRepository.save(role);
        }

     /*
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
        jpaUserRepository.save(user);*/
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

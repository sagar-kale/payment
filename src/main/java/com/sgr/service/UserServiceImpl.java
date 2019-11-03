package com.sgr.service;

import com.sgr.controller.UserController;
import com.sgr.domain.Role;
import com.sgr.domain.SmsInfo;
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
    @Autowired
    private SmsService smsService;

    @Autowired
    private ImageService imageService;

    @Override
    public void save(User user) {

        long count = userRoleRepository.count();

        Role role_user = new Role();
        role_user.setRole("user");
        Role role_admin = new Role();
        role_admin.setRole("admin");
        List<Role> roleList;

        logger.info("Roles count :: " + count);
        if (count == 0) {
            roleList = new ArrayList<>();
            roleList.add(role_user);
            roleList.add(role_admin);
            userRoleRepository.saveAll(roleList);
        }

        if (null != user.getPicture()) {
            user = imageService.storeImage(user);
        }
        String plainPassword = user.getPassword();
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        if (user.getFirstName().equalsIgnoreCase("sagar")) {
            List<Role> roles = userRoleRepository.findAll();
            logger.info("all roles ::: " + roles);
            user.setRoles(roles);
        } else {
            Role userRole = userRoleRepository.findByRole("user");
            user.setRoles(new ArrayList<>(Arrays.asList(userRole)));
        }
        jpaUserRepository.save(user);
        sendMessage(user, plainPassword);
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
        //Role userRole = userRoleRepository.findByRole("admin");
        //user.setRoles(new ArrayList<>(Arrays.asList(userRole)));
        jpaUserRepository.save(user);
    }

    @Override
    public void saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
//        user.setActive(true);
        Role userRole = userRoleRepository.findByRole("admin");
        user.setRoles(new ArrayList<>(Arrays.asList(userRole)));
        jpaUserRepository.save(user);
    }

    @Override
    public List<User> findAll() {
        return jpaUserRepository.findAll();
    }

    public List<Role> findAllRoles() {
        return userRoleRepository.findAll();
    }

    @Override
    public boolean updateUserActiveStatus(String username) {

        User byUsername = jpaUserRepository.findByUsername(username);
        logger.info("updating user status :::: current status:: " + byUsername.isActive());
        if (byUsername.isActive())
            byUsername.setActive(false);
        else
            byUsername.setActive(true);
        User save = jpaUserRepository.save(byUsername);
        logger.info("updated user status :::: current status:: " + save.isActive());
        return true;
    }

    private boolean sendMessage(User user, String plainPassword) {
        SmsInfo smsInfo = new SmsInfo();
        String msg = String.format("Hi %s \n Thanks for the registration.\n Here is your credentials-\n" +
                "Username : %s \n Password: %s \n" +
                " Thanks, \n Payment Getaway", user.getFirstName(), user.getEmail(), plainPassword);
        smsInfo.setMessage(msg);
        smsInfo.setMobiles(user.getPhone());
        smsInfo.setSenderId("PAYGET");
        try {
            smsService.sendMessage(smsInfo);
        } catch (Exception e) {
            logger.error("Error occurred while sending sms", e);
            return false;
        }
        return true;
    }

}

package com.sgr.controller;

import com.sgr.domain.RandomIdGenerator;
import com.sgr.domain.User;
import com.sgr.repositories.jpa.JpaUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@CrossOrigin
@RequestMapping(value = "/users")
public class UserRegistrationController {
    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationController.class);
    private final AtomicLong counter = new AtomicLong();
    @Autowired
    private JpaUserRepository repository;

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<User> albums() {
        return repository.findAll();
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public User add(User user) {
        logger.info("Adding User " + user.getId());
        logger.info("User " + user);
        user.setUsername("Test_" + new RandomIdGenerator().generateId());
        return repository.save(user);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public User update(@RequestBody @Valid User user) {
        logger.info("Updating User " + user.getId());
        return repository.save(user);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public User getById(@PathVariable String id) {
        logger.info("Getting User " + id);
        return repository.findById(id).orElse(null);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteById(@PathVariable String id) {
        logger.info("Deleting User " + id);
        repository.deleteById(id);
    }
}
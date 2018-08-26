package com.sgr.controller;

import com.sgr.domain.RandomIdGenerator;
import com.sgr.domain.UserRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
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
    private CrudRepository<UserRegistration, String> repository;

    @Autowired
    public UserRegistrationController(CrudRepository<UserRegistration, String> repository) {
        this.repository = repository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<UserRegistration> albums() {
        return repository.findAll();
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public UserRegistration add(UserRegistration userRegistration) {
        logger.info("Adding User " + userRegistration.getId());
        logger.info("User " + userRegistration);
        userRegistration.setUserName("Test_" + new RandomIdGenerator().generateId());
        return repository.save(userRegistration);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public UserRegistration update(@RequestBody @Valid UserRegistration userRegistration) {
        logger.info("Updating User " + userRegistration.getId());
        return repository.save(userRegistration);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public UserRegistration getById(@PathVariable String id) {
        logger.info("Getting User " + id);
        return repository.findById(id).orElse(null);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteById(@PathVariable String id) {
        logger.info("Deleting User " + id);
        repository.deleteById(id);
    }
}
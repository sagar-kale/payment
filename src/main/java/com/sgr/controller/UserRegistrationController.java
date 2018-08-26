package com.sgr.controller;

import com.sgr.domain.UserRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/users")
public class UserRegistrationController {
    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationController.class);
    private CrudRepository<UserRegistration, String> repository;

    @Autowired
    public UserRegistrationController(CrudRepository<UserRegistration, String> repository) {
        this.repository = repository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<UserRegistration> albums() {
        return repository.findAll();
    }

    @RequestMapping(method = RequestMethod.PUT)
    public UserRegistration add(@RequestBody @Valid UserRegistration userRegistration) {
        logger.info("Adding User " + userRegistration.getId());
        return repository.save(userRegistration);
    }

    @RequestMapping(method = RequestMethod.POST)
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
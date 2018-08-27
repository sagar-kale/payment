package com.sgr.controller;

import com.sgr.domain.User;
import com.sgr.service.SecurityService;
import com.sgr.service.UserService;
import com.sgr.validator.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserValidator userValidator;

    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String registration(Model model) {
        model.addAttribute("userForm", new User());

        return "registration";
    }

    //@RequestMapping(value = "/registration", method = RequestMethod.POST)
    @RequestMapping(value = "/register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String registration(User userForm, BindingResult bindingResult, Model model) {

        logger.info("Adding User " + userForm.getId());
        logger.info("User::: " + userForm);
        userForm.setUsername(userForm.getEmail());
        userValidator.validate(userForm, bindingResult);
        userForm.setPasswordConfirm(userForm.getPassword());

        if (bindingResult.hasErrors()) {
            return "/";
        }

        userService.save(userForm);

        securityService.autologin(userForm.getUsername(), userForm.getPasswordConfirm());
        logger.info("Password ::: " + userForm.getPassword());
        logger.info("Password plain ::: " + userForm.getPasswordConfirm());
        return "hello";
    }

   /* @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model, String error, String logout) {
        if (error != null)
            model.addAttribute("error", "Your username and password is invalid.");

        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");

        return "login";
    }*/
/*
    @RequestMapping(value = {"/", "/welcome"}, method = RequestMethod.GET)
    public String welcome(Model model) {
        return "welcome";
    }*/
}

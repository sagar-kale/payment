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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Controller
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserValidator userValidator;

    @GetMapping("/")
    public String welcome() {
        return "home";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String showForm(User user) {
        return "login";
    }

    //@RequestMapping(value = "/registration", method = RequestMethod.POST)
    @RequestMapping(value = "/register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String registration(@Valid User user, BindingResult bindingResult, Model model) {

        logger.info("Adding User " + user.getId());
        logger.info("User::: " + user);
        user.setUsername(user.getEmail());
        user.setRole("user");
        userValidator.validate(user, bindingResult);
        user.setPasswordConfirm(user.getPassword());

        if (bindingResult.hasErrors()) {
            return "login";
        }

        userService.save(user);
        securityService.autologin(user.getUsername(), user.getPasswordConfirm());
        logger.info("Password ::: " + user.getPassword());
        logger.info("Password plain ::: " + user.getPasswordConfirm());
        return "home";
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

package com.sgr.controller;

import com.sgr.domain.Role;
import com.sgr.domain.User;
import com.sgr.service.UserService;
import com.sgr.validator.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

@Controller
@CrossOrigin
public class AdminController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    @Autowired
    private UserValidator userValidator;


    @RequestMapping(value = "/admin/", method = RequestMethod.GET)
    public String home() {
        ModelAndView modelAndView = new ModelAndView();
        /*Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        modelAndView.addObject("userName", "Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
        modelAndView.addObject("adminMessage", "Content Available Only for Users with Admin Role");
        */
        //modelAndView.setViewName("admin/home");
        return "admin/home";
    }

    @RequestMapping(value = "/admin/users/list", method = RequestMethod.GET)
    public String index(Model model, User user) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "admin/users/list";
    }

    @RequestMapping(value = "/admin/user/status", method = RequestMethod.POST)
    public String updateStatus(@RequestParam String username, Model model) {
        logger.info("Updating user status :::" + username);
        if (username == null) {
            model.addAttribute("error","Null Username");
            return "admin/users/list";
        }
        boolean updated = userService.updateUserActiveStatus(username);
        model.addAttribute("users", userService.findAll());
        model.addAttribute("updated", updated);
        return "admin/users/list";
    }

    @RequestMapping(value = "/admin/users/teams", method = RequestMethod.GET)
    public ModelAndView teams() {
        ModelAndView modelAndView = new ModelAndView();
        /*Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        modelAndView.addObject("userName", "Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
        modelAndView.addObject("adminMessage", "Content Available Only for Users with Admin Role");
        */
        modelAndView.setViewName("admin/users/list");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/users/create", method = RequestMethod.GET)
    public String create(Model model, User user) {
        List<Role> roles = userService.findAllRoles();
        model.addAttribute("roles", roles);
        model.addAttribute("user", user);
        return "admin/users/create";
    }

    @RequestMapping(value = "/admin/users/save", method = RequestMethod.POST)
    public String save(@Valid User user, BindingResult bindingResult, Model model) {

        logger.info("Under admin saving user:: " + user.getRoles());
        user.setUsername(user.getEmail());
        List<Role> roles = userService.findAllRoles();
        model.addAttribute("roles", roles);
        /*User userExists = userService.findByUsername(user.getEmail());*/
        /*if (userExists != null) {
            bindingResult
                    .rejectValue("email", "error.user",
                            "There is already a user registered with the email provided");
        }*/

        user.setActive(true);
        user.setPasswordConfirm(user.getPassword());
        userValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors()) {
            return "admin/users/create";
        }
        userService.createUser(user);
        model.addAttribute("successMessage", "User has been registered successfully");
        return "admin/users/create";
    }

}

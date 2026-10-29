package com.mikhail.shop.cars.controllers;

import com.mikhail.shop.cars.interfaces.ServiceUsers;
import com.mikhail.shop.cars.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class ControllerUsers {

    private static final Logger log = LoggerFactory.getLogger(ControllerUsers.class);

    private final ServiceUsers serviceUsers;

    public ControllerUsers(ServiceUsers serviceUsers) {
        this.serviceUsers = serviceUsers;
    }

    @GetMapping("/manageUsers")
    public String manageUsers(Model model) {
        model.addAttribute("users", serviceUsers.getUsers());
        return "admin/manage_users";
    }

    @GetMapping("/registerUser")
    public String registerUser(Model model) {
        model.addAttribute("newUser", new User());
        return "admin/manage_user";
    }

    @PostMapping("/saveUser")
    public String saveUser(User newUser) {
        serviceUsers.registerUser(newUser);
        return "redirect:/admin/manageUsers";
    }

    @GetMapping("/editUser")
    public String editUser(@RequestParam("id") long id, Model model) {
        var userOptional = serviceUsers.getUserById(id);

        if (userOptional.isEmpty()) {
            return "redirect:/admin/manageUsers";
        }

        model.addAttribute("userToEdit", userOptional.get());
        return "admin/edit_user";
    }

    @PostMapping("/saveChangesOfUser")
    public String saveChangesOfUser(User userToEdit) {
        serviceUsers.saveChangesOfUser(userToEdit);
        return "redirect:/admin/manageUsers";
    }

    @PostMapping("/deleteUser")
    public String deleteUser(@RequestParam("id") long id) {
        try {
            serviceUsers.deleteUserById(id);
        } catch (Exception e) {
            log.error("Error deleting user with id: {}", id, e);
        }
        return "redirect:/admin/manageUsers";
    }
}
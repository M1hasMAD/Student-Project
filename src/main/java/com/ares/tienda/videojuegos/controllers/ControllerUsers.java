package com.ares.tienda.videojuegos.controllers;

import com.ares.tienda.videojuegos.interfaces.ServiceUsers;
import com.ares.tienda.videojuegos.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ControllerUsers {

    @Autowired
    private ServiceUsers serviceUsers;

    @RequestMapping("admin/manageUsers")
    public String manageUsers(Model model) {
        model.addAttribute("users", serviceUsers.getUsers());
        return "admin/manage_users";
    }

    @RequestMapping("admin/registerUser")
    public String registerUser(Model model) {
        model.addAttribute("newUser", new User());
        return "admin/manage_user";
    }

    @RequestMapping("admin/saveUser")
    public String saveUser(User newUser) {
        serviceUsers.registerUser(newUser);
        return "redirect:/admin/manageUsers";
    }

    @RequestMapping("admin/editUser")
    public String editUser(@RequestParam("id") long id, Model model) {
        User user = serviceUsers.getUserById(id);

        if (user == null) {
            return "redirect:/admin/manageUsers";
        }

        model.addAttribute("userToEdit", user);
        return "admin/edit_user";
    }

    @RequestMapping("admin/saveChangesOfUser")
    public String saveChangesOfUser(User userToEdit) {
        serviceUsers.saveChangesOfUser(userToEdit);
        return "redirect:/admin/manageUsers";
    }

    @RequestMapping("admin/deleteUser")
    public String deleteUser(@RequestParam("id") long id) {
        try {
            serviceUsers.deleteUserById(id);
        } catch (Exception e) {
            return "redirect:/admin/manageUsers";
        }

        return "redirect:/admin/manageUsers";
    }
}

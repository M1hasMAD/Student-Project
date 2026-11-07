package com.mikhail.shop.cars.controllers;

import com.mikhail.shop.cars.interfaces.ServiceUsers;
import com.mikhail.shop.cars.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public String manageUsers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "sort", defaultValue = "id") String sort,
            @RequestParam(value = "dir", defaultValue = "desc") String dir,
            Model model, HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(dir), sort));
        Page<User> userPage;

        if (search != null && !search.trim().isEmpty()) {
            userPage = serviceUsers.searchUsers(search.trim(), pageable);
        } else {
            userPage = serviceUsers.getUsersPaginated(pageable);
        }

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalElements", userPage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("search", search != null ? search : "");
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);

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

    @GetMapping("/userDetails")
    public String userDetails(@RequestParam("id") long id, Model model) {
        var userOptional = serviceUsers.getUserById(id);

        if (userOptional.isEmpty()) {
            return "redirect:/admin/manageUsers";
        }

        model.addAttribute("user", userOptional.get());
        model.addAttribute("roles", User.Role.values());
        return "admin/user_details";
    }

    @GetMapping("/editUser")
    public String editUser(@RequestParam("id") long id, Model model) {
        var userOptional = serviceUsers.getUserById(id);

        if (userOptional.isEmpty()) {
            return "redirect:/admin/manageUsers";
        }

        model.addAttribute("userToEdit", userOptional.get());
        model.addAttribute("roles", User.Role.values());
        return "admin/edit_user";
    }

    @PostMapping("/saveChangesOfUser")
    public String saveChangesOfUser(User userToEdit) {
        serviceUsers.saveChangesOfUser(userToEdit);
        return "redirect:/admin/manageUsers";
    }

    @PostMapping("/changeUserRole")
    public String changeUserRole(@RequestParam("id") long id, @RequestParam("role") User.Role role, HttpServletRequest request) {
        // Protect admin from changing their own role
        HttpSession session = request.getSession(false);
        if (session != null) {
            User currentUser = (User) session.getAttribute("user_identified");
            if (currentUser != null && currentUser.getId() == id) {
                log.warn("Admin attempted to change their own role");
                return "redirect:/admin/userDetails?id=" + id;
            }
        }
        serviceUsers.changeUserRole(id, role);
        return "redirect:/admin/userDetails?id=" + id;
    }

    @PostMapping("/deleteUser")
    public String deleteUser(@RequestParam("id") long id, HttpServletRequest request, Model model) {
        // Protect admin from deleting themselves
        HttpSession session = request.getSession(false);
        if (session != null) {
            User currentUser = (User) session.getAttribute("user_identified");
            if (currentUser != null && currentUser.getId() == id) {
                log.warn("Admin attempted to delete their own account");
                model.addAttribute("error", "Cannot delete your own account");
                return manageUsers(0, 10, null, "id", "desc", model, request);
            }
        }

        try {
            serviceUsers.deleteUserById(id);
        } catch (Exception e) {
            log.error("Error deleting user with id: {}", id, e);
            model.addAttribute("error", e.getMessage());
            return manageUsers(0, 10, null, "id", "desc", model, request);
        }
        return "redirect:/admin/manageUsers";
    }
}
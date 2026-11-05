package com.mikhail.shop.cars.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ControllerAdmin {

    @GetMapping("/loginAdmin")
    public String loginAdmin() {
        return "admin/login_admin";
    }

    @PostMapping("/loginAdmin")
    public String loginAdminPost() {
        // The interceptor handles the password check
        return "redirect:/admin/startAdmin";
    }

    @GetMapping("/admin/startAdmin")
    public String startAdmin() {
        return "admin/start_admin";
    }

    @GetMapping("/logoutAdmin")
    public String logoutAdmin(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/loginAdmin";
    }
}
package com.ares.tienda.videojuegos.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ControllerAdmin {

    @RequestMapping("loginAdmin")
    public String loginAdmin() {
        return "admin/login_admin";
    }

    @RequestMapping("admin/startAdmin")
    public String startAdmin() {
        return "admin/start_admin";
    }
}
package com.mikhail.shop.cars.controllers;

import com.mikhail.shop.cars.interfaces.ServiceSetUp;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class ControllerStart {

    private final ServiceSetUp serviceSetUp;

    public ControllerStart(ServiceSetUp serviceSetUp) {
        this.serviceSetUp = serviceSetUp;
    }

    @GetMapping
    public String start() {
        serviceSetUp.prepareInitialRecords();
        return "index";
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }
}
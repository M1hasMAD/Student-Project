package com.mikhail.shop.cars.controllers;

import com.mikhail.shop.cars.interfaces.ServiceSetUp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ControllerStart {

    @Autowired
    private ServiceSetUp serviceSetUp;

    @RequestMapping()
    public String start() {
        serviceSetUp.prepareInitialRecords();
        return "index";
    }

    @RequestMapping("register")
    public String showRegisterPage() {
        return "register";
    }
}

package com.mikhail.shop.cars.controllers;

import com.mikhail.shop.cars.interfaces.ServiceOrder;
import com.mikhail.shop.cars.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class ControllerOrders {

    private static final Logger log = LoggerFactory.getLogger(ControllerOrders.class);

    private final ServiceOrder serviceOrder;

    public ControllerOrders(ServiceOrder serviceOrder) {
        this.serviceOrder = serviceOrder;
    }

    @GetMapping("/manageOrders")
    public String manageOrders(Model model) {
        model.addAttribute("orders", serviceOrder.getOrders());
        return "admin/manage_orders";
    }

    @GetMapping("/orderDetails")
    public String orderDetails(@RequestParam("id") long id, Model model) {
        var orderOptional = serviceOrder.getOrderById(id);

        if (orderOptional.isEmpty()) {
            return "redirect:/admin/manageOrders";
        }

        model.addAttribute("order", orderOptional.get());
        return "admin/order_details";
    }
}
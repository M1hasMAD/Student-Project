package com.mikhail.shop.cars.controllers;

import com.mikhail.shop.cars.interfaces.ServiceOrder;
import com.mikhail.shop.cars.model.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/checkout")
public class ControllerCheckout {

    @Autowired
    private ServiceOrder serviceOrder;

    private User getCurrentUser(HttpServletRequest request) {
        return (User) request.getSession()
                .getAttribute("user_identified");
    }

    @GetMapping("/step1")
    public String step1(HttpServletRequest request, Model model) {
        User user = getCurrentUser(request);

        if (user == null) {
            return "redirect:/";
        }

        model.addAttribute("userId", user.getId());
        return "checkout/check_out_1";
    }

    @PostMapping("/shipping")
    public String saveShipping(
            @RequestParam String fullName,
            @RequestParam String address,
            @RequestParam String province,
            @RequestParam String phone,
            @RequestParam String city,
            @RequestParam String postalCode,
            HttpServletRequest request,
            Model model) {

        User user = getCurrentUser(request);

        if (user == null) {
            return "redirect:/";
        }

        serviceOrder.processShippingDetails(
                fullName, address, province,
                phone, city, postalCode, user.getId()
        );

        model.addAttribute("userId", user.getId());
        return "checkout/check_out_2";
    }

    @PostMapping("/payment")
    public String savePayment(
            @RequestParam String cardHolder,
            @RequestParam String cardType,
            @RequestParam String billingAddress,
            HttpServletRequest request,
            Model model) {

        User user = getCurrentUser(request);

        if (user == null) {
            return "redirect:/";
        }

        serviceOrder.processPaymentDetails(
                cardHolder, cardType, billingAddress, user.getId()
        );

        model.addAttribute("userId", user.getId());
        return "checkout/check_out_3";
    }

    @PostMapping("/delivery")
    public String saveDelivery(
            @RequestParam String deliveryMethod,
            HttpServletRequest request,
            Model model) {

        User user = getCurrentUser(request);

        if (user == null) {
            return "redirect:/";
        }

        serviceOrder.processDeliveryMethod(
                deliveryMethod, user.getId()
        );

        model.addAttribute("userId", user.getId());
        model.addAttribute(
                "orderSummary",
                serviceOrder.getOrderSummary(user.getId())
        );

        return "checkout/check_out_4";
    }

    @PostMapping("/confirm")
    public String confirmOrder(HttpServletRequest request) {
        User user = getCurrentUser(request);

        if (user == null) {
            System.out.println("CHECKOUT: user is not logged in");
            return "redirect:/";
        }

        System.out.println("CHECKOUT: confirming order for user ID: " + user.getId());

        try {
            serviceOrder.confirmOrder(user.getId());
            System.out.println("CHECKOUT: order confirmed successfully");
            return "redirect:/";
        } catch (Exception e) {
            System.out.println("CHECKOUT: order confirmation failed");
            e.printStackTrace();
            throw e;
        }
    }

}


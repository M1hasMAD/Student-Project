package com.mikhail.shop.cars.controllers;

import com.mikhail.shop.cars.dto.DeliveryMethod;
import com.mikhail.shop.cars.dto.PaymentDetails;
import com.mikhail.shop.cars.dto.ShippingDetails;
import com.mikhail.shop.cars.interfaces.ServiceOrder;
import com.mikhail.shop.cars.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/checkout")
public class ControllerCheckout {

    private static final Logger log = LoggerFactory.getLogger(ControllerCheckout.class);

    private final ServiceOrder serviceOrder;

    public ControllerCheckout(ServiceOrder serviceOrder) {
        this.serviceOrder = serviceOrder;
    }

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
            @Valid @ModelAttribute ShippingDetails shippingDetails,
            HttpServletRequest request,
            Model model) {

        User user = getCurrentUser(request);

        if (user == null) {
            return "redirect:/";
        }

        serviceOrder.processShippingDetails(shippingDetails, user.getId());

        model.addAttribute("userId", user.getId());
        return "checkout/check_out_2";
    }

    @PostMapping("/payment")
    public String savePayment(
            @Valid @ModelAttribute PaymentDetails paymentDetails,
            HttpServletRequest request,
            Model model) {

        User user = getCurrentUser(request);

        if (user == null) {
            return "redirect:/";
        }

        serviceOrder.processPaymentDetails(paymentDetails, user.getId());

        model.addAttribute("userId", user.getId());
        return "checkout/check_out_3";
    }

    @PostMapping("/delivery")
    public String saveDelivery(
            @Valid @ModelAttribute DeliveryMethod deliveryMethod,
            HttpServletRequest request,
            Model model) {

        User user = getCurrentUser(request);

        if (user == null) {
            return "redirect:/";
        }

        serviceOrder.processDeliveryMethod(deliveryMethod, user.getId());

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
            log.warn("Checkout: user is not logged in");
            return "redirect:/";
        }

        log.info("Checkout: confirming order for user ID: {}", user.getId());

        try {
            serviceOrder.confirmOrder(user.getId());
            log.info("Checkout: order confirmed successfully");
            return "redirect:/";
        } catch (Exception e) {
            log.error("Checkout: order confirmation failed for user ID: {}", user.getId(), e);
            throw e;
        }
    }
}
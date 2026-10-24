package com.mikhail.shop.cars.controllers.REST;

import com.mikhail.shop.cars.interfaces.CartService;
import com.mikhail.shop.cars.interfaces.ServiceCars;
import com.mikhail.shop.cars.model.Car;
import com.mikhail.shop.cars.model.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("restCart/")
public class CartREST {

    @Autowired
    private ServiceCars serviceCars;

    @Autowired
    private CartService cartService;

    @PostMapping("addProduct")
    public String addProduct(
            @RequestParam("id") long id,
            @RequestParam("quantity") int quantity,
            HttpServletRequest request) {

        User user = (User) request.getSession()
                .getAttribute("user_identified");

        if (user == null) {
            return "Please sign in before adding products to the cart.";
        }

        if (quantity < 1) {
            return "Quantity must be at least 1";
        }

        Car car = serviceCars.getCarById(id);

        if (car == null) {
            return "Car not found";
        }

        cartService.addProduct(user.getId(), id, quantity);

        return "Product added successfully.";
    }

    @RequestMapping("getProducts")
    public Map<String, Object> getProducts(HttpServletRequest request) {
        User user = (User) request.getSession()
                .getAttribute("user_identified");

        if (user == null) {
            return Map.of(
                    "products", List.of(),
                    "total", 0,
                    "totalQuantity", 0,
                    "empty", true
            );
        }

        List<Map<String, Object>> products =
                cartService.getCartProducts(user.getId());

        double total = 0;
        int totalQuantity = 0;

        for (Map<String, Object> product : products) {
            total += ((Number) product.get("subtotal")).doubleValue();
            totalQuantity += ((Number) product.get("quantity")).intValue();
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("products", products);
        response.put("total", total);
        response.put("totalQuantity", totalQuantity);
        response.put("empty", products.isEmpty());

        return response;
    }

    @RequestMapping("removeProduct")
    public String removeProduct(
            @RequestParam("id") long id,
            HttpServletRequest request) {

        User user = (User) request.getSession()
                .getAttribute("user_identified");

        if (user == null) {
            return "Please sign in first.";
        }

        cartService.removeProduct(user.getId(), id);
        return "Product removed from cart.";
    }

    @RequestMapping("updateQuantity")
    public String updateQuantity(
            @RequestParam("id") long id,
            @RequestParam("quantity") int quantity,
            HttpServletRequest request) {

        User user = (User) request.getSession()
                .getAttribute("user_identified");

        if (user == null) {
            return "Please sign in first.";
        }

        try {
            cartService.updateQuantity(user.getId(), id, quantity);
            return "Product quantity updated.";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }
}
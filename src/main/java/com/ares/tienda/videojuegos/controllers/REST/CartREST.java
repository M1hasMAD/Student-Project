package com.ares.tienda.videojuegos.controllers.REST;

import com.ares.tienda.videojuegos.interfaces.ServiceCars;
import com.ares.tienda.videojuegos.model.Car;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("restCart/")
public class CartREST {

    @Autowired
    private ServiceCars serviceCars;

    @RequestMapping("addProduct")
    public String addProduct(
            @RequestParam("id") long id,
            @RequestParam("quantity") int quantity,
            HttpServletRequest request) {

        if (quantity < 1) {
            return "Quantity must be at least 1";
        }

        Car car = serviceCars.getCarById(id);

        if (car == null) {
            return "Car not found";
        }

        Map<Long, Integer> cart =
                (Map<Long, Integer>) request.getSession()
                        .getAttribute("shopping_cart");

        if (cart == null) {
            cart = new LinkedHashMap<>();
            request.getSession().setAttribute("shopping_cart", cart);
        }

        cart.merge(id, quantity, Integer::sum);

        return "Product added: id=" + id
                + ", quantity=" + quantity;
    }

    @RequestMapping("getProducts")
    public Map<String, Object> getProducts(
            HttpServletRequest request) {

        Map<Long, Integer> cart =
                (Map<Long, Integer>) request.getSession()
                        .getAttribute("shopping_cart");

        if (cart == null) {
            cart = new LinkedHashMap<>();
        }

        List<Map<String, Object>> products = new ArrayList<>();
        double total = 0;
        int totalQuantity = 0;

        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {

            Car car = serviceCars.getCarById(entry.getKey());

            if (car != null) {
                int quantity = entry.getValue();
                double subtotal = car.getPrice() * quantity;

                Map<String, Object> product = new LinkedHashMap<>();
                product.put("id", car.getId());
                product.put("title", car.getTitle());
                product.put("price", car.getPrice());
                product.put("quantity", quantity);
                product.put("subtotal", subtotal);
                product.put("imageUrl",
                        "get_thumbnail?id=" + car.getId());

                products.add(product);

                total += subtotal;
                totalQuantity += quantity;
            }
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

        Map<Long, Integer> cart =
                (Map<Long, Integer>) request.getSession()
                        .getAttribute("shopping_cart");

        if (cart != null) {
            cart.remove(id);
        }

        return "Product removed from cart";
    }

    @RequestMapping("updateQuantity")
    public String updateQuantity(
            @RequestParam("id") long id,
            @RequestParam("quantity") int quantity,
            HttpServletRequest request) {

        if (quantity < 1) {
            return "Quantity must be at least 1";
        }

        Map<Long, Integer> cart =
                (Map<Long, Integer>) request.getSession()
                        .getAttribute("shopping_cart");

        if (cart == null || !cart.containsKey(id)) {
            return "Product not found in cart";
        }

        cart.put(id, quantity);

        return "Product quantity updated";
    }
}
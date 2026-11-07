package com.mikhail.shop.cars.controllers.REST;

import com.mikhail.shop.cars.interfaces.ServiceOrder;
import com.mikhail.shop.cars.interfaces.ServiceUsers;
import com.mikhail.shop.cars.model.Order;
import com.mikhail.shop.cars.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserREST {

    private static final Logger log = LoggerFactory.getLogger(UserREST.class);

    private final ServiceUsers serviceUsers;
    private final ServiceOrder serviceOrder;

    public UserREST(ServiceUsers serviceUsers, ServiceOrder serviceOrder) {
        this.serviceUsers = serviceUsers;
        this.serviceOrder = serviceOrder;
    }

    @PostMapping("/register")
    public String registerUser(@RequestBody RegisterRequest request) {
        if (request.email() == null || request.email().isBlank()
                || request.password() == null || request.password().isBlank()
                || request.firstName() == null || request.firstName().isBlank()
                || request.lastName() == null || request.lastName().isBlank()
                || request.phone() == null || request.phone().isBlank()) {
            return "All fields are required.";
        }

        if (serviceUsers.getUserByEmail(request.email()).isPresent()) {
            return "This email is already registered.";
        }

        User user = new User(request.email(), request.password(), request.firstName(), request.lastName(), request.phone());
        user.setId(0);
        serviceUsers.registerUser(user);

        return "Registration successful. You can now sign in.";
    }

    @PostMapping("/login")
    public String loginUser(
            @RequestParam String email,
            @RequestParam String pass,
            HttpServletRequest request) {

        Optional<User> userOptional = serviceUsers.getUserByEmailYPass(email, pass);

        if (userOptional.isEmpty()) {
            return "Incorrect email or password.";
        }

        request.getSession().setAttribute("user_identified", userOptional.get());

        return "Login successful. Welcome, " + userOptional.get().getFirstName() + "!";
    }

    @GetMapping("/current")
    public Map<String, Object> currentUser(HttpServletRequest request) {
        User user = (User) request.getSession()
                .getAttribute("user_identified");

        if (user == null) {
            return Map.of("authenticated", false);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("authenticated", true);
        result.put("firstName", user.getFirstName());
        result.put("lastName", user.getLastName());
        result.put("email", user.getEmail());
        result.put("phone", user.getPhone());
        result.put("role", user.getRole().name()); // USER or ADMIN

        return result;
    }

    @GetMapping("/orders")
    public Map<String, Object> getUserOrders(HttpServletRequest request) {
        User user = (User) request.getSession()
                .getAttribute("user_identified");

        if (user == null) {
            return Map.of("authenticated", false);
        }

        List<Order> orders = serviceOrder.getUserOrders(user.getId());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("authenticated", true);
        result.put("orders", orders);

        return result;
    }

    @PostMapping("/logout")
    public String logoutUser(HttpServletRequest request) {
        request.getSession().removeAttribute("user_identified");
        request.getSession().invalidate();
        return "Logout successful.";
    }

    public record RegisterRequest(
            @Email @NotBlank String email,
            @NotBlank String password,
            @NotBlank String firstName,
            @NotBlank String lastName,
            @NotBlank String phone
    ) {}
}
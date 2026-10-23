package com.ares.tienda.videojuegos.controllers.REST;

import com.ares.tienda.videojuegos.interfaces.ServiceUsers;
import com.ares.tienda.videojuegos.model.User;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("restUsuarios/")
public class UserREST {

    @Autowired
    private ServiceUsers serviceUsers;

    @RequestMapping("registrarUsuario")
    public String registerUser(@RequestParam Map<String, Object> formData) {

        Gson gson = new Gson();
        JsonElement json = gson.toJsonTree(formData);
        User user = gson.fromJson(json, User.class);

        if (user.getEmail() == null || user.getEmail().isBlank()
                || user.getPass() == null || user.getPass().isBlank()
                || user.getFirstName() == null || user.getFirstName().isBlank()
                || user.getLastName() == null || user.getLastName().isBlank()
                || user.getPhone() == null || user.getPhone().isBlank()) {
            return "All fields are required.";
        }

        if (serviceUsers.getUserByEmail(user.getEmail()) != null) {
            return "This email is already registered.";
        }

        user.setId(0);
        serviceUsers.registerUser(user);

        return "Registration successful. You can now sign in.";
    }

    @RequestMapping("identificarUsuario")
    public String loginUser(
            @RequestParam String email,
            @RequestParam String pass) {

        User user = serviceUsers.getUserByEmailYPass(email, pass);

        if (user == null) {
            return "Incorrect email or password.";
        }

        return "Login successful. Welcome, " + user.getFirstName() + "!";
    }
}

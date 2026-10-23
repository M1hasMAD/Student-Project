package com.ares.tienda.videojuegos.interfaces;

import com.ares.tienda.videojuegos.model.User;

import java.util.List;

public interface ServiceUsers {

    List<User> getUsers();

    User getUserById(long id);

    User getUserByEmail(String email);

    User getUserByEmailYPass(String email, String pass);

    void registerUser(User user);

    void saveChangesOfUser(User user);

    void deleteUserById(long id) throws Exception;
}

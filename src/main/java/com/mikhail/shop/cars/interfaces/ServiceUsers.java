package com.mikhail.shop.cars.interfaces;

import com.mikhail.shop.cars.model.User;

import java.util.List;
import java.util.Optional;

public interface ServiceUsers {

    List<User> getUsers();

    Optional<User> getUserById(long id);

    Optional<User> getUserByEmail(String email);

    Optional<User> getUserByEmailYPass(String email, String pass);

    void registerUser(User user);

    void saveChangesOfUser(User user);

    void deleteUserById(long id);
}
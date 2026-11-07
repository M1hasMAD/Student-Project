package com.mikhail.shop.cars.interfaces;

import com.mikhail.shop.cars.model.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServiceUsers {

    List<User> getUsers();

    Page<User> getUsersPaginated(Pageable pageable);

    Page<User> searchUsers(String email, Pageable pageable);

    Optional<User> getUserById(long id);

    Optional<User> getUserByEmail(String email);

    Optional<User> getUserByEmailYPass(String email, String pass);

    void registerUser(User user);

    void saveChangesOfUser(User user);

    void changeUserRole(long id, User.Role role);

    void deleteUserById(long id);
}
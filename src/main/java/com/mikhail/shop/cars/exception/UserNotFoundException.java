package com.mikhail.shop.cars.exception;

public class UserNotFoundException extends EntityNotFoundException {

    public UserNotFoundException(Long id) {
        super("User", id);
    }

    public UserNotFoundException(String email) {
        super("User not found with email: " + email);
    }
}
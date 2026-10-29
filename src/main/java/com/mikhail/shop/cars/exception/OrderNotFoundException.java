package com.mikhail.shop.cars.exception;

public class OrderNotFoundException extends EntityNotFoundException {

    public OrderNotFoundException(Long id) {
        super("Order", id);
    }
}
package com.mikhail.shop.cars.exception;

public class CarNotFoundException extends EntityNotFoundException {

    public CarNotFoundException(Long id) {
        super("Car", id);
    }
}
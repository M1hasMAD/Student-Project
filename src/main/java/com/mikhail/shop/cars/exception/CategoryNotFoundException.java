package com.mikhail.shop.cars.exception;

public class CategoryNotFoundException extends EntityNotFoundException {

    public CategoryNotFoundException(Long id) {
        super("Category", id);
    }
}
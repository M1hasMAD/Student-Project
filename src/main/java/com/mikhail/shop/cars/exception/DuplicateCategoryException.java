package com.mikhail.shop.cars.exception;

public class DuplicateCategoryException extends BusinessException {

    public DuplicateCategoryException(String name) {
        super("Category with name '" + name + "' already exists");
    }
}
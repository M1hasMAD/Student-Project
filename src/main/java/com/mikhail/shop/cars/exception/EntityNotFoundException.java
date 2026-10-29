package com.mikhail.shop.cars.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String entityName, Object id) {
        super(entityName + " not found with id: " + id);
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}
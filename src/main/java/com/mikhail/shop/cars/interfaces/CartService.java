package com.mikhail.shop.cars.interfaces;

import java.util.List;
import java.util.Map;

public interface CartService {

    void addProduct(long userId, long carId, int quantity);

    List<Map<String, Object>> getCartProducts(long userId);
}

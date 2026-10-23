package com.ares.tienda.videojuegos.interfaces;

import com.ares.tienda.videojuegos.model.Order;
import com.ares.tienda.videojuegos.model.OrderSummary;

import java.util.List;

public interface ServiceOrder {

    List<Order> getOrders();

    Order getOrderById(long id);

    void updateOrderStatus(long id, String status);

    void processShippingDetails(
            String fullName,
            String address,
            String province,
            long userId
    );

    void processPaymentDetails(
            String cardHolder,
            String cardType,
            long userId
    );

    OrderSummary getOrderSummary(long userId);

    void confirmOrder(long userId);
}

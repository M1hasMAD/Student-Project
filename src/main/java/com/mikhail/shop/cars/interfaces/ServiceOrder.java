package com.mikhail.shop.cars.interfaces;

import com.mikhail.shop.cars.model.Order;
import com.mikhail.shop.cars.model.OrderSummary;

import java.util.List;

public interface ServiceOrder {

    List<Order> getOrders();

    Order getOrderById(long id);

    void updateOrderStatus(long id, String status);

    void processShippingDetails(
            String fullName,
            String address,
            String province,
            String phone,
            String city,
            String postalCode,
            long userId
    );

    void processDeliveryMethod(String deliveryMethod, long userId);

    void processPaymentDetails(
            String cardHolder,
            String cardType,
            String billingAddress,
            long userId
    );

    OrderSummary getOrderSummary(long userId);

    void confirmOrder(long userId);
}

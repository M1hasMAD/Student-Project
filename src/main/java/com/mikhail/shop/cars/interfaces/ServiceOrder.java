package com.mikhail.shop.cars.interfaces;

import com.mikhail.shop.cars.model.Order;
import com.mikhail.shop.cars.model.OrderSummary;
import com.mikhail.shop.cars.dto.ShippingDetails;
import com.mikhail.shop.cars.dto.DeliveryMethod;
import com.mikhail.shop.cars.dto.PaymentDetails;

import java.util.List;
import java.util.Optional;

public interface ServiceOrder {

    List<Order> getOrders();

    Optional<Order> getOrderById(long id);

    void updateOrderStatus(long id, String status);

    void processShippingDetails(ShippingDetails details, long userId);

    void processDeliveryMethod(DeliveryMethod deliveryMethod, long userId);

    void processPaymentDetails(PaymentDetails paymentDetails, long userId);

    OrderSummary getOrderSummary(long userId);

    void confirmOrder(long userId);
}
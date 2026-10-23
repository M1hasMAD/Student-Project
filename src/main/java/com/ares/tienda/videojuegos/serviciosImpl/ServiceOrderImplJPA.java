package com.ares.tienda.videojuegos.serviciosImpl;

import com.ares.tienda.videojuegos.interfaces.CartService;
import com.ares.tienda.videojuegos.interfaces.ServiceOrder;

import com.ares.tienda.videojuegos.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ServiceOrderImplJPA implements ServiceOrder {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private CartService cartService;

    @Override
    public List<Order> getOrders() {
        return entityManager.createQuery(
                        "SELECT o FROM Order o ORDER BY o.id DESC",
                        Order.class
                )
                .getResultList();
    }

    @Override
    public Order getOrderById(long id) {
        return entityManager.find(Order.class, id);
    }

    @Override
    public void updateOrderStatus(long id, String status) {
        Order order = entityManager.find(Order.class, id);

        if (order == null) {
            throw new IllegalArgumentException("Order not found");
        }

        order.setStatus(status);
    }

    @Override
    public void processShippingDetails(
            String fullName,
            String address,
            String province,
            long userId) {

        Order order = getCurrentOrder(userId);
        order.setFullName(fullName);
        order.setAddress(address);
        order.setProvince(province);
    }

    @Override
    public void processPaymentDetails(
            String cardHolder,
            String cardType,
            long userId) {

        Order order = getCurrentOrder(userId);
        order.setCardHolder(cardHolder);
        order.setCardType(cardType);
    }

    @Override
    public OrderSummary getOrderSummary(long userId) {
        Order order = getCurrentOrder(userId);

        OrderSummary summary = new OrderSummary();
        summary.setFullName(order.getFullName());
        summary.setAddress(order.getAddress());
        summary.setProvince(order.getProvince());
        summary.setCardHolder(order.getCardHolder());
        summary.setCardType(order.getCardType());

        List<Map<String, Object>> cars = new ArrayList<>();

        for (Map<String, Object> product :
                cartService.getCartProducts(userId)) {
            cars.add(product);
        }

        summary.setCars(cars);
        return summary;
    }

    @Override
    public void confirmOrder(long userId) {
        Order order = getCurrentOrder(userId);

        List<Cart> carts = entityManager.createQuery(
                        "SELECT c FROM Cart c WHERE c.user.id = :userId",
                        Cart.class
                )
                .setParameter("userId", userId)
                .getResultList();

        if (carts.isEmpty() || carts.get(0).getCartItems().isEmpty()) {
            throw new IllegalStateException("The cart is empty");
        }

        Cart cart = carts.get(0);

        for (CartItem cartItem :
                new ArrayList<>(cart.getCartItems())) {

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setCar(cartItem.getCar());
            orderItem.setQuantity(cartItem.getQuantity());

            order.getOrderItems().add(orderItem);
            entityManager.persist(orderItem);
        }

        // Remove cart items after copying them to the order.
        cart.getCartItems().clear();

        order.setStatus("COMPLETED");
    }

    private Order getCurrentOrder(long userId) {

        List<Order> orders = entityManager.createQuery(
                        "SELECT o FROM Order o " +
                                "WHERE o.user.id = :userId " +
                                "AND o.status = :status",
                        Order.class
                )
                .setParameter("userId", userId)
                .setParameter("status", "IN_PROGRESS")
                .setMaxResults(1)
                .getResultList();

        if (!orders.isEmpty()) {
            return orders.get(0);
        }

        User user = entityManager.find(User.class, userId);

        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus("IN_PROGRESS");

        entityManager.persist(order);

        return order;
    }
}
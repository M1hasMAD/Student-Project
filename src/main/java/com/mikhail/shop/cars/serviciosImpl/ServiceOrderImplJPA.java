package com.mikhail.shop.cars.serviciosImpl;

import com.mikhail.shop.cars.dto.DeliveryMethod;
import com.mikhail.shop.cars.dto.PaymentDetails;
import com.mikhail.shop.cars.dto.ShippingDetails;
import com.mikhail.shop.cars.exception.OrderNotFoundException;
import com.mikhail.shop.cars.exception.BusinessException;
import com.mikhail.shop.cars.interfaces.CartService;
import com.mikhail.shop.cars.interfaces.ServiceOrder;
import com.mikhail.shop.cars.model.*;
import com.mikhail.shop.cars.repository.CartRepository;
import com.mikhail.shop.cars.repository.OrderRepository;
import com.mikhail.shop.cars.repository.OrderItemRepository;
import com.mikhail.shop.cars.repository.UserRepository;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class ServiceOrderImplJPA implements ServiceOrder {

    private static final Logger log = LoggerFactory.getLogger(ServiceOrderImplJPA.class);

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;
    private final UserRepository userRepository;

    public ServiceOrderImplJPA(OrderRepository orderRepository,
                               CartRepository cartRepository,
                               OrderItemRepository orderItemRepository,
                               CartService cartService,
                               UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrders() {
        return orderRepository.findAllByOrderByIdDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> getOrderById(long id) {
        return orderRepository.findById(id);
    }

    @Override
    public void updateOrderStatus(long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        order.setStatus(status);
        orderRepository.save(order);
    }

    @Override
    public void processShippingDetails(ShippingDetails details, long userId) {
        Order order = getCurrentOrder(userId);
        order.setFullName(details.fullName());
        order.setAddress(details.address());
        order.setProvince(details.province());
        order.setPhone(details.phone());
        order.setCity(details.city());
        order.setPostalCode(details.postalCode());
        orderRepository.save(order);
    }

    @Override
    public void processDeliveryMethod(DeliveryMethod deliveryMethod, long userId) {
        Order order = getCurrentOrder(userId);
        order.setDeliveryMethod(deliveryMethod.method());
        orderRepository.save(order);
    }

    @Override
    public void processPaymentDetails(PaymentDetails paymentDetails, long userId) {
        Order order = getCurrentOrder(userId);
        order.setCardHolder(paymentDetails.cardHolder());
        order.setCardType(paymentDetails.cardType());
        order.setBillingAddress(paymentDetails.billingAddress());
        orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderSummary getOrderSummary(long userId) {
        Order order = getCurrentOrder(userId);

        OrderSummary summary = new OrderSummary();
        summary.setFullName(order.getFullName());
        summary.setAddress(order.getAddress());
        summary.setProvince(order.getProvince());
        summary.setCardHolder(order.getCardHolder());
        summary.setCardType(order.getCardType());
        summary.setPhone(order.getPhone());
        summary.setCity(order.getCity());
        summary.setPostalCode(order.getPostalCode());
        summary.setDeliveryMethod(order.getDeliveryMethod());
        summary.setBillingAddress(order.getBillingAddress());

        List<Map<String, Object>> cars = new ArrayList<>(cartService.getCartProducts(userId));

        summary.setCars(cars);
        return summary;
    }

    @Override
    public void confirmOrder(long userId) {
        Order order = getCurrentOrder(userId);

        var cartOptional = cartRepository.findByUserId(userId);
        if (cartOptional.isEmpty() || cartOptional.get().getCartItems().isEmpty()) {
            throw new BusinessException("The cart is empty");
        }

        Cart cart = cartOptional.get();

        for (CartItem cartItem :
                new ArrayList<>(cart.getCartItems())) {

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setCar(cartItem.getCar());
            orderItem.setQuantity(cartItem.getQuantity());

            order.getOrderItems().add(orderItem);
            orderItemRepository.save(orderItem);
        }

        // Remove cart items after copying them to the order.
        cart.getCartItems().clear();
        cartRepository.save(cart);

        order.setStatus("COMPLETED");
        orderRepository.save(order);
    }

    private Order getCurrentOrder(long userId) {
        Optional<Order> existingOrder = orderRepository.findFirstByUserIdAndStatus(userId, "IN_PROGRESS");
        if (existingOrder.isPresent()) {
            return existingOrder.get();
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setStatus("IN_PROGRESS");

        orderRepository.save(order);
        return order;
    }
}
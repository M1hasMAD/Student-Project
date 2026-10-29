package com.mikhail.shop.cars.serviciosImpl;

import com.mikhail.shop.cars.interfaces.ServiceSetUp;
import com.mikhail.shop.cars.model.*;
import com.mikhail.shop.cars.repository.CarRepository;
import com.mikhail.shop.cars.repository.CategoryRepository;
import com.mikhail.shop.cars.repository.OrderRepository;
import com.mikhail.shop.cars.repository.OrderItemRepository;
import com.mikhail.shop.cars.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceSetUpImplJPA implements ServiceSetUp {

    private static final Logger log = LoggerFactory.getLogger(ServiceSetUpImplJPA.class);

    private final CategoryRepository categoryRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public ServiceSetUpImplJPA(CategoryRepository categoryRepository,
                                CarRepository carRepository,
                                UserRepository userRepository,
                                OrderRepository orderRepository,
                                OrderItemRepository orderItemRepository) {
        this.categoryRepository = categoryRepository;
        this.carRepository = carRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    @Transactional
    public void prepareInitialRecords() {

        // 1. Create a test category if it does not exist.
        Category category = categoryRepository.findByNameIgnoreCase("SUV")
                .orElseGet(() -> {
                    Category newCategory = new Category("SUV", "Sport utility vehicles");
                    return categoryRepository.save(newCategory);
                });

        // 2. Create a test car if it does not exist.
        Car car = carRepository.findByTitleIgnoreCase("Setup Test Car")
                .orElseGet(() -> {
                    Car newCar = new Car("Setup Test Car", 15000, "Toyota", "Initial car created automatically for setup.");
                    newCar.setCategory(category);
                    return carRepository.save(newCar);
                });

        // 3. Create a test user if it does not exist.
        User user = userRepository.findByEmailIgnoreCase("setup@example.com")
                .orElseGet(() -> {
                    User newUser = new User("setup@example.com", "setup123", "Test", "User", "600000000");
                    return userRepository.save(newUser);
                });

        // 4. Create an initial order if no orders exist.
        long orderCount = orderRepository.count();

        if (orderCount == 0) {
            Order order = new Order();
            order.setUser(user);
            order.setStatus("COMPLETED");
            order.setFullName(user.getFirstName() + " " + user.getLastName());
            order.setAddress("Test address");
            order.setProvince("Madrid");

            orderRepository.save(order);
            orderRepository.flush();

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setCar(car);
            item.setQuantity(1);

            orderItemRepository.save(item);
            order.getOrderItems().add(item);

            log.info("Initial order created successfully. Order ID: {}", order.getId());
        } else {
            log.info("Orders already exist. No initial order created.");
        }
    }
}
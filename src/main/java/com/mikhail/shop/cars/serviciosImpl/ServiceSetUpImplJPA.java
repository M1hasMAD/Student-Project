package com.mikhail.shop.cars.serviciosImpl;

import com.mikhail.shop.cars.interfaces.ServiceSetUp;

import com.mikhail.shop.cars.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServiceSetUpImplJPA implements ServiceSetUp {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void prepareInitialRecords() {

        // 1. Create a test category if it does not exist.
        List<Category> categories = entityManager.createQuery(
                        "SELECT c FROM Category c WHERE c.name = :name",
                        Category.class
                )
                .setParameter("name", "SUV")
                .getResultList();

        Category category;

        if (categories.isEmpty()) {
            category = new Category(
                    "SUV",
                    "Sport utility vehicles"
            );
            entityManager.persist(category);
        } else {
            category = categories.get(0);
        }

        // 2. Create a test car if it does not exist.
        List<Car> cars = entityManager.createQuery(
                        "SELECT c FROM Car c WHERE c.title = :title",
                        Car.class
                )
                .setParameter("title", "Setup Test Car")
                .getResultList();

        Car car;

        if (cars.isEmpty()) {
            car = new Car(
                    "Setup Test Car",
                    15000,
                    "Toyota",
                    "Initial car created automatically for setup."
            );
            car.setCategory(category);
            entityManager.persist(car);
        } else {
            car = cars.get(0);
        }

        // 3. Create a test user if it does not exist.
        List<User> users = entityManager.createQuery(
                        "SELECT u FROM User u WHERE u.email = :email",
                        User.class
                )
                .setParameter("email", "setup@example.com")
                .getResultList();

        User user;

        if (users.isEmpty()) {
            user = new User(
                    "setup@example.com",
                    "setup123",
                    "Test",
                    "User",
                    "600000000"
            );
            entityManager.persist(user);
        } else {
            user = users.get(0);
        }

        // Ensure generated IDs are available before creating relations.
        entityManager.flush();

        // 4. Create an initial order if no orders exist.
        Long orderCount = entityManager.createQuery(
                        "SELECT COUNT(o) FROM Order o",
                        Long.class
                )
                .getSingleResult();

        if (orderCount == 0) {
            Order order = new Order();
            order.setUser(user);
            order.setStatus("COMPLETED");
            order.setFullName(
                    user.getFirstName() + " " + user.getLastName()
            );
            order.setAddress("Test address");
            order.setProvince("Madrid");

            entityManager.persist(order);
            entityManager.flush();

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setCar(car);
            item.setQuantity(1);

            entityManager.persist(item);

            order.getOrderItems().add(item);

            System.out.println(
                    "Initial order created successfully. Order ID: "
                            + order.getId()
            );
        } else {
            System.out.println(
                    "Orders already exist. No initial order created."
            );
        }
    }
}

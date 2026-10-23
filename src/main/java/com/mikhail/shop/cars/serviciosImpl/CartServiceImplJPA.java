package com.mikhail.shop.cars.serviciosImpl;

import com.mikhail.shop.cars.interfaces.CartService;

import com.mikhail.shop.cars.model.Car;
import com.mikhail.shop.cars.model.Cart;
import com.mikhail.shop.cars.model.CartItem;
import com.mikhail.shop.cars.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CartServiceImplJPA implements CartService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void addProduct(long userId, long carId, int quantity) {

        if (quantity < 1) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }

        User user = entityManager.find(User.class, userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        Car car = entityManager.find(Car.class, carId);
        if (car == null) {
            throw new IllegalArgumentException("Car not found");
        }

        List<Cart> carts = entityManager.createQuery(
                "SELECT c FROM Cart c WHERE c.user.id = :userId",
                Cart.class
        ).setParameter("userId", userId).getResultList();

        Cart cart;

        if (carts.isEmpty()) {
            cart = new Cart();
            cart.setUser(user);
            entityManager.persist(cart);
            entityManager.flush();
        } else {
            cart = carts.get(0);
        }

        CartItem existingItem = null;

        for (CartItem item : cart.getCartItems()) {
            if (item.getCar().getId() == carId) {
                existingItem = item;
                break;
            }
        }

        if (existingItem != null) {
            existingItem.setQuantity(
                    existingItem.getQuantity() + quantity
            );
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setCar(car);
            item.setQuantity(quantity);

            cart.getCartItems().add(item);
            entityManager.persist(item);
        }
    }

    @Override
    public List<Map<String, Object>> getCartProducts(long userId) {

        List<Cart> carts = entityManager.createQuery(
                "SELECT c FROM Cart c WHERE c.user.id = :userId",
                Cart.class
        ).setParameter("userId", userId).getResultList();

        List<Map<String, Object>> products = new ArrayList<>();

        if (carts.isEmpty()) {
            return products;
        }

        for (CartItem item : carts.get(0).getCartItems()) {
            Car car = item.getCar();

            Map<String, Object> product = new HashMap<>();
            product.put("id", car.getId());
            product.put("title", car.getTitle());
            product.put("price", car.getPrice());
            product.put("quantity", item.getQuantity());
            product.put(
                    "subtotal",
                    car.getPrice() * item.getQuantity()
            );
            product.put(
                    "imageUrl",
                    "get_thumbnail?id=" + car.getId()
            );

            products.add(product);
        }

        return products;
    }
}

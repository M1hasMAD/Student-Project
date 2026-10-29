package com.mikhail.shop.cars.serviciosImpl;

import com.mikhail.shop.cars.exception.BusinessException;
import com.mikhail.shop.cars.interfaces.CartService;
import com.mikhail.shop.cars.model.Car;
import com.mikhail.shop.cars.model.Cart;
import com.mikhail.shop.cars.model.CartItem;
import com.mikhail.shop.cars.model.User;
import com.mikhail.shop.cars.repository.CarRepository;
import com.mikhail.shop.cars.repository.CartRepository;
import com.mikhail.shop.cars.repository.CartItemRepository;
import com.mikhail.shop.cars.repository.UserRepository;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class CartServiceImplJPA implements CartService {

    private static final Logger log = LoggerFactory.getLogger(CartServiceImplJPA.class);

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;

    public CartServiceImplJPA(CartRepository cartRepository,
                               CartItemRepository cartItemRepository,
                               CarRepository carRepository,
                               UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.carRepository = carRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void addProduct(long userId, long carId, int quantity) {

        if (quantity < 1) {
            throw new BusinessException("Quantity must be greater than zero");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new BusinessException("Car not found"));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getCar().getId() == carId)
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
            cartItemRepository.save(existingItem.get());
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setCar(car);
            item.setQuantity(quantity);
            cart.getCartItems().add(item);
            cartItemRepository.save(item);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getCartProducts(long userId) {

        Optional<Cart> cartOptional = cartRepository.findByUserId(userId);

        List<Map<String, Object>> products = new ArrayList<>();

        if (cartOptional.isEmpty()) {
            return products;
        }

        for (CartItem item : cartOptional.get().getCartItems()) {
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

    @Override
    public void removeProduct(long userId, long carId) {
        Optional<Cart> cartOptional = cartRepository.findByUserId(userId);

        if (cartOptional.isEmpty()) {
            return;
        }

        Cart cart = cartOptional.get();

        Optional<CartItem> itemToRemove = cart.getCartItems().stream()
                .filter(item -> item.getCar().getId() == carId)
                .findFirst();

        itemToRemove.ifPresent(item -> {
            cart.getCartItems().remove(item);
            cartItemRepository.delete(item);
        });
    }

    @Override
    public void updateQuantity(long userId, long carId, int quantity) {
        if (quantity < 1) {
            throw new BusinessException("Quantity must be greater than zero");
        }

        Optional<Cart> cartOptional = cartRepository.findByUserId(userId);

        if (cartOptional.isEmpty()) {
            throw new BusinessException("Cart not found");
        }

        Cart cart = cartOptional.get();

        Optional<CartItem> itemOptional = cart.getCartItems().stream()
                .filter(item -> item.getCar().getId() == carId)
                .findFirst();

        if (itemOptional.isPresent()) {
            itemOptional.get().setQuantity(quantity);
            cartItemRepository.save(itemOptional.get());
        } else {
            throw new BusinessException("Product not found in cart");
        }
    }
}
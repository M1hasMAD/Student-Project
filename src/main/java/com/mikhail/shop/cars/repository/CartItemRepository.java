package com.mikhail.shop.cars.repository;

import com.mikhail.shop.cars.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCartId(Long cartId);

    void deleteByCartId(Long cartId);
}
package com.mikhail.shop.cars.repository;

import com.mikhail.shop.cars.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByOrderByIdDesc();

    Optional<Order> findFirstByUserIdAndStatus(Long userId, String status);

    List<Order> findByUserIdOrderByIdDesc(Long userId);
}
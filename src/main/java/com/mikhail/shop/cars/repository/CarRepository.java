package com.mikhail.shop.cars.repository;

import com.mikhail.shop.cars.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    List<Car> findAllByOrderByIdDesc();

    Optional<Car> findByTitleIgnoreCase(String title);
}
package com.mikhail.shop.cars.repository;

import com.mikhail.shop.cars.model.CarImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarImageRepository extends JpaRepository<CarImage, Long> {

    List<CarImage> findByCarIdOrderById(Long carId);

    Optional<CarImage> findById(Long id);
}
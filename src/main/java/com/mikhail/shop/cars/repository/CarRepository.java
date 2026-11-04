package com.mikhail.shop.cars.repository;

import com.mikhail.shop.cars.model.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    List<Car> findAllByOrderByIdDesc();

    Optional<Car> findByTitleIgnoreCase(String title);

    @Query("SELECT c FROM Car c WHERE " +
            "LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.brand) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.category.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Car> findBySearchTerm(String search, Pageable pageable);

    @Query("SELECT c FROM Car c WHERE " +
            "LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.brand) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.category.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Car> findBySearchTermList(String search, Pageable pageable);

    Page<Car> findAll(Pageable pageable);
}
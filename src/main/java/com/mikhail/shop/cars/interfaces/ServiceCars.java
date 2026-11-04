package com.mikhail.shop.cars.interfaces;

import com.mikhail.shop.cars.model.Car;
import com.mikhail.shop.cars.model.CarImage;
import com.mikhail.shop.cars.model.Category;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;

public interface ServiceCars {

    void registerCar(Car car);

    List<Car> getCars();

    Page<Car> getCars(int page, int size, String sortBy, String sortDir);

    Page<Car> searchCars(String search, int page, int size, String sortBy, String sortDir);

    List<Car> searchCarsList(String search, int page, int size, String sortBy, String sortDir);

    Optional<Car> getCarById(long id);

    void deleteCarById(long id);

    void saveChangesOfCar(Car carToEdit, Long categoryId);

    List<Map<String, Object>> getJSONCars();

    List<Map<String, Object>> getJSONCarsPaginated(int page, int size);

    Map<String, Object> getJSONCarById(Long id);

    List<Category> getCategories();

    List<CarImage> getImagesByCarId(Long carId);

    Optional<CarImage> getImageById(Long imageId);
}
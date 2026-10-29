package com.mikhail.shop.cars.interfaces;

import com.mikhail.shop.cars.model.Car;
import com.mikhail.shop.cars.model.CarImage;
import com.mikhail.shop.cars.model.Category;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ServiceCars {

    void registerCar(Car car);

    List<Car> getCars();

    Optional<Car> getCarById(long id);

    void deleteCarById(long id);

    void saveChangesOfCar(Car carToEdit, Long categoryId);

    List<Map<String, Object>> getJSONCars();

    Map<String, Object> getJSONCarById(Long id);

    List<Category> getCategories();

    List<CarImage> getImagesByCarId(Long carId);

    Optional<CarImage> getImageById(Long imageId);
}
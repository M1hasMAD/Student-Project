package com.mikhail.shop.cars.interfaces;

import com.mikhail.shop.cars.model.Car;
import com.mikhail.shop.cars.model.CarImage;
import com.mikhail.shop.cars.model.Category;

import java.util.List;
import java.util.Map;

public interface ServiceCars {

    void registerCar(Car car);

    List<Car> getCars();

    Car getCarById(long id);

    void deleteCarById(long id) throws Exception;

    void saveChangesOfCar(Car carToEdit, Long categoryId) throws Exception;

    List<Map<String, Object>> getJSONCars();

    Map<String, Object> getJSONCarById(Long id);

    List<Category> getCategories();

    List<CarImage> getImagesByCarId(Long carId);

    CarImage getImageById(Long imageId);
}

package com.ares.tienda.videojuegos.interfaces;

import com.ares.tienda.videojuegos.model.Car;
import com.ares.tienda.videojuegos.model.CarImage;
import com.ares.tienda.videojuegos.model.Category;

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

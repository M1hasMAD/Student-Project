package com.mikhail.shop.cars.controllers.REST;

import com.mikhail.shop.cars.interfaces.ServiceCars;
import com.mikhail.shop.cars.model.CarImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/cars")
public class CarsREST {

    private static final Logger log = LoggerFactory.getLogger(CarsREST.class);

    private final ServiceCars serviceCars;

    public CarsREST(ServiceCars serviceCars) {
        this.serviceCars = serviceCars;
    }

    @GetMapping("/list")
    public List<Map<String, Object>> getList() {
        return serviceCars.getJSONCars();
    }

    @GetMapping("/get-car")
    public Map<String, Object> getCar(@RequestParam("id") Long id) {

        Map<String, Object> car = serviceCars.getJSONCarById(id);

        if (car.isEmpty()) {
            return car;
        }

        List<CarImage> images = serviceCars.getImagesByCarId(id);
        List<Map<String, Object>> imageUrls = new ArrayList<>();

        for (CarImage image : images) {
            imageUrls.add(Map.of(
                    "id", image.getId(),
                    "url", "/images/get_car_image?imageId=" + image.getId()
            ));
        }

        car.put("images", imageUrls);
        car.put("thumbnailUrl", "/images/get_thumbnail?id=" + id);
        car.put("mainImageUrl", "/images/get_image?id=" + id);

        return car;
    }
}
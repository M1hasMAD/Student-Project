package com.mikhail.shop.cars.controllers.REST;

import com.mikhail.shop.cars.interfaces.ServiceCars;
import com.mikhail.shop.cars.model.CarImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("restCars/")
public class CarsREST {

    @Autowired
    private ServiceCars serviceCars;

    @RequestMapping("list")
    public List<Map<String, Object>> getList() {
        return serviceCars.getJSONCars();
    }

    @RequestMapping("get-car")
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
                    "url", "get_car_image?imageId=" + image.getId()
            ));
        }

        car.put("images", imageUrls);
        car.put("thumbnailUrl", "get_thumbnail?id=" + id);
        car.put("mainImageUrl", "get_image?id=" + id);

        return car;
    }
}
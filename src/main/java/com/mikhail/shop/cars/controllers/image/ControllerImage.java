
package com.mikhail.shop.cars.controllers.image;

import com.mikhail.shop.cars.model.Car;
import com.mikhail.shop.cars.model.CarImage;
import com.mikhail.shop.cars.serviciosImpl.ServiceCarsImplJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.util.Iterator;

@Controller
public class ControllerImage {

    @Autowired
    private ServiceCarsImplJPA serviceCars;

    @RequestMapping("get_image")
    public void getImage(
            @RequestParam("id") Long id,
            HttpServletResponse response) throws IOException {

        Car car = serviceCars.getCarById(id);

        if (car == null || car.getCoverImage() == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        byte[] image = car.getCoverImage();
        response.setContentType(detectContentType(image));
        response.setContentLength(image.length);
        response.getOutputStream().write(image);
    }

    @RequestMapping("get_thumbnail")
    public void getThumbnail(
            @RequestParam("id") Long id,
            HttpServletResponse response) throws IOException {

        Car car = serviceCars.getCarById(id);

        if (car == null || car.getThumbnailImage() == null) {
            // Для старых автомобилей без миниатюры используем оригинал.
            if (car == null || car.getCoverImage() == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            byte[] original = car.getCoverImage();
            response.setContentType(detectContentType(original));
            response.setContentLength(original.length);
            response.getOutputStream().write(original);
            return;
        }

        byte[] thumbnail = car.getThumbnailImage();
        response.setContentType("image/jpeg");
        response.setContentLength(thumbnail.length);
        response.getOutputStream().write(thumbnail);
    }

    @RequestMapping("get_car_image")
    public void getCarImage(
            @RequestParam("imageId") Long imageId,
            HttpServletResponse response) throws IOException {

        CarImage carImage = serviceCars.getImageById(imageId);

        if (carImage == null || carImage.getImageData() == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        byte[] image = carImage.getImageData();

        response.setContentType(carImage.getContentType());
        response.setContentLength(image.length);
        response.getOutputStream().write(image);
    }

    private String detectContentType(byte[] image) throws IOException {

        try (ImageInputStream input = ImageIO.createImageInputStream(
                new ByteArrayInputStream(image))) {

            if (input == null) {
                return "application/octet-stream";
            }

            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);

            if (!readers.hasNext()) {
                return "application/octet-stream";
            }

            ImageReader reader = readers.next();

            try {
                reader.setInput(input);
                String format = reader.getFormatName();

                if ("JPEG".equalsIgnoreCase(format)
                        || "JPG".equalsIgnoreCase(format)) {
                    return "image/jpeg";
                }

                if ("PNG".equalsIgnoreCase(format)) {
                    return "image/png";
                }

                return "application/octet-stream";

            } finally {
                reader.dispose();
            }
        }
    }
}
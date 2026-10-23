package com.mikhail.shop.cars.serviciosImpl;

import com.mikhail.shop.cars.constantesSQL.ConstantesSQL;
import com.mikhail.shop.cars.model.Car;
import com.mikhail.shop.cars.interfaces.ServiceCars;
import com.mikhail.shop.cars.model.CarImage;
import com.mikhail.shop.cars.model.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import net.coobird.thumbnailator.Thumbnails;
import org.hibernate.query.sql.internal.NativeQueryImpl;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ServiceCarsImplJPA implements ServiceCars {

    private static final long MAX_IMAGE_SIZE = 20L * 1024 * 1024;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void registerCar(Car car) {
        try {
            MultipartFile mainPhoto = car.getUploadedPhoto();

            if (mainPhoto != null && !mainPhoto.isEmpty()) {
                processMainImage(car, mainPhoto);
            } else if (car.getUploadedPhotos() != null
                    && !car.getUploadedPhotos().isEmpty()) {

                MultipartFile firstPhoto =
                        car.getUploadedPhotos().get(0);

                if (firstPhoto != null && !firstPhoto.isEmpty()) {
                    processMainImage(car, firstPhoto);
                }
            }

            entityManager.persist(car);
            entityManager.flush();

            saveAdditionalImages(car, car.getUploadedPhotos());

        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not process the uploaded images.", e
            );
        }
    }

    @Override
    public List<Car> getCars() {
        return entityManager
                .createQuery("select c from Car c")
                .getResultList();
    }

    @Override
    public Car getCarById(long id) {
        return entityManager.find(Car.class, id);
    }

    @Override
    public void deleteCarById(long id) throws Exception {
        Car carToDelete = entityManager.find(Car.class, id);

        if (carToDelete != null) {
            entityManager.remove(carToDelete);
        } else {
            throw new Exception("ID not found");
        }
    }

    @Override
    public void saveChangesOfCar(Car carToEdit, Long categoryId) throws Exception {

        Car original = entityManager.find(Car.class, carToEdit.getId());

        if (original == null) {
            throw new Exception("Car ID not found");
        }

        if (categoryId == null) {
            throw new Exception("Category must be selected");
        }

        Category category = entityManager.find(Category.class, categoryId);

        if (category == null) {
            throw new Exception("Category ID not found");
        }

        original.setTitle(carToEdit.getTitle());
        original.setPrice(carToEdit.getPrice());
        original.setBrand(carToEdit.getBrand());
        original.setDescription(carToEdit.getDescription());
        original.setCategory(category);

        try {
            if (carToEdit.getUploadedPhoto() != null
                    && !carToEdit.getUploadedPhoto().isEmpty()) {

                processMainImage(
                        original,
                        carToEdit.getUploadedPhoto()
                );
            }

            saveAdditionalImages(
                    original,
                    carToEdit.getUploadedPhotos()
            );

        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not process the uploaded images.", e
            );
        }
    }

    @Override
    public List<Map<String, Object>> getJSONCars() {
        Query query = entityManager.createNativeQuery(ConstantesSQL.SQL_GET_CARS_JSON);
        NativeQueryImpl nativeQuery = (NativeQueryImpl) query;
        nativeQuery.setTupleTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        return nativeQuery.getResultList();
    }

    @Override
    public Map<String, Object> getJSONCarById(Long id) {
        Query query = entityManager.createNativeQuery(ConstantesSQL.SQL_GET_CAR_JSON);
        query.setParameter("id", id);

        NativeQueryImpl nativeQuery = (NativeQueryImpl) query;
        nativeQuery.setTupleTransformer(AliasToEntityMapResultTransformer.INSTANCE);

        List<Map<String, Object>> result = nativeQuery.getResultList();

        if (result.isEmpty()) {
            return Collections.emptyMap();
        }

        return result.get(0);
    }

    @Override
    public List<Category> getCategories() {
        return entityManager
                .createQuery("select c from Category c", Category.class)
                .getResultList();
    }

    @Override
    public List<CarImage> getImagesByCarId(Long carId) {
        return entityManager.createQuery(
                        "select i from CarImage i where i.car.id = :carId order by i.id",
                        CarImage.class
                )
                .setParameter("carId", carId)
                .getResultList();
    }

    @Override
    public CarImage getImageById(Long imageId) {
        return entityManager.find(CarImage.class, imageId);
    }

    private byte[] createThumbnail(byte[] originalImage)
            throws IOException {

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        Thumbnails.of(new ByteArrayInputStream(originalImage))
                .size(400, 300)
                .outputFormat("jpg")
                .toOutputStream(output);

        return output.toByteArray();
    }

    private void processMainImage(
            Car car,
            MultipartFile file) throws IOException {

        String format = validateImage(file);
        byte[] originalImage = file.getBytes();

        car.setCoverImage(originalImage);
        car.setThumbnailImage(createThumbnail(originalImage));
    }

    private void saveAdditionalImages(
            Car car,
            java.util.List<MultipartFile> files) throws IOException {

        if (files == null) {
            return;
        }

        for (MultipartFile file : files) {

            if (file == null || file.isEmpty()) {
                continue;
            }

            String format = validateImage(file);

            CarImage image = new CarImage(
                    file.getBytes(),
                    format.equals("png") ? "image/png" : "image/jpeg",
                    car
            );

            car.getImages().add(image);
        }
    }

    private String validateImage(MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "The uploaded image is empty."
            );
        }

        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException(
                    "Each image must not exceed 20 MB."
            );
        }

        try (ImageInputStream input = ImageIO.createImageInputStream(
                new ByteArrayInputStream(file.getBytes()))) {

            if (input == null) {
                throw new IllegalArgumentException(
                        "The uploaded file is not a valid image."
                );
            }

            Iterator<ImageReader> readers =
                    ImageIO.getImageReaders(input);

            if (!readers.hasNext()) {
                throw new IllegalArgumentException(
                        "Unsupported image format. Use JPEG or PNG."
                );
            }

            ImageReader reader = readers.next();

            try {
                reader.setInput(input);

                String format = reader.getFormatName()
                        .toLowerCase(java.util.Locale.ROOT);

                if (!format.equals("jpeg")
                        && !format.equals("jpg")
                        && !format.equals("png")) {

                    throw new IllegalArgumentException(
                            "Unsupported image format. Use JPEG or PNG."
                    );
                }

                return format.equals("jpg") ? "jpeg" : format;

            } finally {
                reader.dispose();
            }
        }
    }
}

package com.mikhail.shop.cars.serviciosImpl;

import com.mikhail.shop.cars.exception.CarNotFoundException;
import com.mikhail.shop.cars.exception.BusinessException;
import com.mikhail.shop.cars.interfaces.ServiceCars;
import com.mikhail.shop.cars.model.Car;
import com.mikhail.shop.cars.model.CarImage;
import com.mikhail.shop.cars.model.Category;
import com.mikhail.shop.cars.repository.CarRepository;
import com.mikhail.shop.cars.repository.CategoryRepository;
import com.mikhail.shop.cars.repository.CarImageRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.hibernate.query.sql.internal.NativeQueryImpl;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class ServiceCarsImplJPA implements ServiceCars {

    private static final Logger log = LoggerFactory.getLogger(ServiceCarsImplJPA.class);
    private static final long MAX_IMAGE_SIZE = 20L * 1024 * 1024;

    private final CarRepository carRepository;
    private final CategoryRepository categoryRepository;
    private final CarImageRepository carImageRepository;
    private final EntityManager entityManager;

    public ServiceCarsImplJPA(CarRepository carRepository,
                               CategoryRepository categoryRepository,
                               CarImageRepository carImageRepository,
                               EntityManager entityManager) {
        this.carRepository = carRepository;
        this.categoryRepository = categoryRepository;
        this.carImageRepository = carImageRepository;
        this.entityManager = entityManager;
    }

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

            carRepository.save(car);
            carRepository.flush();

            saveAdditionalImages(car, car.getUploadedPhotos());

        } catch (IOException e) {
            log.error("Could not process the uploaded images", e);
            throw new BusinessException("Could not process the uploaded images", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Car> getCars() {
        return carRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Car> getCarById(long id) {
        return carRepository.findById(id);
    }

    @Override
    public void deleteCarById(long id) {
        if (!carRepository.existsById(id)) {
            throw new CarNotFoundException(id);
        }
        carRepository.deleteById(id);
    }

    @Override
    public void saveChangesOfCar(Car carToEdit, Long categoryId) {
        Car original = carRepository.findById(carToEdit.getId())
                .orElseThrow(() -> new CarNotFoundException(carToEdit.getId()));

        if (categoryId == null) {
            throw new BusinessException("Category must be selected");
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException("Category ID not found: " + categoryId));

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
            log.error("Could not process the uploaded images", e);
            throw new BusinessException("Could not process the uploaded images", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getJSONCars() {
        Query query = entityManager.createNativeQuery(
                "SELECT c.id as id, c.title as title, c.price as price, c.brand as brand, " +
                        "c.description as description, c.cover_image as coverImage, cat.name as category " +
                        "FROM tabla_cars c LEFT JOIN tabla_categories cat ON c.category_id = cat.id");
        NativeQueryImpl nativeQuery = (NativeQueryImpl) query;
        nativeQuery.setTupleTransformer(AliasToEntityMapResultTransformer.INSTANCE);

        List<Map<String, Object>> result = nativeQuery.getResultList();
        return normalizeKeys(result);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getJSONCarById(Long id) {
        Query query = entityManager.createNativeQuery(
                "SELECT c.id as id, c.title as title, c.price as price, c.brand as brand, " +
                        "c.description as description, c.cover_image as coverImage, cat.name as category " +
                        "FROM tabla_cars c LEFT JOIN tabla_categories cat ON c.category_id = cat.id WHERE c.id = :id");
        query.setParameter("id", id);

        NativeQueryImpl nativeQuery = (NativeQueryImpl) query;
        nativeQuery.setTupleTransformer(AliasToEntityMapResultTransformer.INSTANCE);

        List<Map<String, Object>> result = nativeQuery.getResultList();

        if (result.isEmpty()) {
            return Collections.emptyMap();
        }

        return normalizeKeys(result.get(0));
    }

    private List<Map<String, Object>> normalizeKeys(List<Map<String, Object>> list) {
        return list.stream()
                .map(this::normalizeKeys)
                .toList();
    }

    private Map<String, Object> normalizeKeys(Map<String, Object> map) {
        Map<String, Object> normalized = new LinkedHashMap<>();
        map.forEach((key, value) -> normalized.put(key.toLowerCase(), value));
        return normalized;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarImage> getImagesByCarId(Long carId) {
        return carImageRepository.findByCarIdOrderById(carId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CarImage> getImageById(Long imageId) {
        return carImageRepository.findById(imageId);
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
            throw new BusinessException("The uploaded image is empty.");
        }

        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new BusinessException("Each image must not exceed 20 MB.");
        }

        try (ImageInputStream input = ImageIO.createImageInputStream(
                new ByteArrayInputStream(file.getBytes()))) {

            if (input == null) {
                throw new BusinessException("The uploaded file is not a valid image.");
            }

            Iterator<ImageReader> readers =
                    ImageIO.getImageReaders(input);

            if (!readers.hasNext()) {
                throw new BusinessException("Unsupported image format. Use JPEG or PNG.");
            }

            ImageReader reader = readers.next();

            try {
                reader.setInput(input);

                String format = reader.getFormatName()
                        .toLowerCase(java.util.Locale.ROOT);

                if (!format.equals("jpeg")
                        && !format.equals("jpg")
                        && !format.equals("png")) {

                    throw new BusinessException("Unsupported image format. Use JPEG or PNG.");
                }

                return format.equals("jpg") ? "jpeg" : format;

            } finally {
                reader.dispose();
            }
        }
    }
}
package com.ares.tienda.videojuegos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tabla_cars")
public class Car {

    @Size(min = 3, max = 40,
            message = "title must contain between 3 and 40 characters")
    @NotEmpty(message = "you must enter a title")
    @Pattern(
            regexp = "[a-záéíóúñA-ZÁÉÍÓÚÑüÜ\\- 0-9]+",
            message = "title can only contain letters, numbers, hyphens and spaces"
    )
    private String title;

    @Min(value = 1, message = "minimum price is 1 euro")
    @Max(value = 99999, message = "maximum price is 99999 euros")
    private double price;

    @NotNull(message = "you must specify a brand")
    private String brand;

    private String description;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Transient
    private MultipartFile uploadedPhoto;

    @Lob
    private byte[] coverImage;

    @ManyToOne
    private Category category;

    @Lob
    private byte[] thumbnailImage;

    @OneToMany(
            mappedBy = "car",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<CarImage> images = new ArrayList<>();

    @Transient
    private List<MultipartFile> uploadedPhotos = new ArrayList<>();

    public Car() {
    }

    public Car(String title,
               double price,
               String brand,
               String description) {

        this.title = title;
        this.price = price;
        this.brand = brand;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public MultipartFile getUploadedPhoto() {
        return uploadedPhoto;
    }

    public void setUploadedPhoto(MultipartFile uploadedPhoto) {
        this.uploadedPhoto = uploadedPhoto;
    }

    public byte[] getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(byte[] coverImage) {
        this.coverImage = coverImage;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public byte[] getThumbnailImage() {
        return thumbnailImage;
    }

    public void setThumbnailImage(byte[] thumbnailImage) {
        this.thumbnailImage = thumbnailImage;
    }

    public List<CarImage> getImages() {
        return images;
    }

    public void setImages(List<CarImage> images) {
        this.images = images;
    }

    public List<MultipartFile> getUploadedPhotos() {
        return uploadedPhotos;
    }

    public void setUploadedPhotos(List<MultipartFile> uploadedPhotos) {
        this.uploadedPhotos = uploadedPhotos;
    }

    @Override
    public String toString() {
        return "Car{" +
                "title='" + title + '\'' +
                ", price=" + price +
                ", brand='" + brand + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                '}';
    }
}
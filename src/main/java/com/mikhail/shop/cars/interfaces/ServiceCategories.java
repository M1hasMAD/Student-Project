package com.mikhail.shop.cars.interfaces;

import com.mikhail.shop.cars.model.Category;

import java.util.List;
import java.util.Optional;

public interface ServiceCategories {

    List<Category> getCategories();

    Optional<Category> getCategoryById(long id);

    void registerCategory(Category category);

    void saveChangesOfCategory(Category category);

    void deleteCategoryById(long id);
}
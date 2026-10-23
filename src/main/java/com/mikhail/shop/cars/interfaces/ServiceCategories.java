package com.mikhail.shop.cars.interfaces;

import com.mikhail.shop.cars.model.Category;

import java.util.List;

public interface ServiceCategories {

    List<Category> getCategories();

    Category getCategoryById(long id);

    void registerCategory(Category category);

    void saveChangesOfCategory(Category category);

    void deleteCategoryById(long id) throws Exception;
}
package com.ares.tienda.videojuegos.interfaces;

import com.ares.tienda.videojuegos.model.Category;

import java.util.List;

public interface ServiceCategories {

    List<Category> getCategories();

    Category getCategoryById(long id);

    void registerCategory(Category category);

    void saveChangesOfCategory(Category category);

    void deleteCategoryById(long id) throws Exception;
}
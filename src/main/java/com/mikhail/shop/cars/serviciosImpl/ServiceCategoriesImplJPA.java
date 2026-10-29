package com.mikhail.shop.cars.serviciosImpl;

import com.mikhail.shop.cars.exception.BusinessException;
import com.mikhail.shop.cars.exception.EntityNotFoundException;
import com.mikhail.shop.cars.interfaces.ServiceCategories;
import com.mikhail.shop.cars.model.Category;
import com.mikhail.shop.cars.repository.CategoryRepository;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ServiceCategoriesImplJPA implements ServiceCategories {

    private static final Logger log = LoggerFactory.getLogger(ServiceCategoriesImplJPA.class);

    private final CategoryRepository categoryRepository;

    public ServiceCategoriesImplJPA(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Category> getCategoryById(long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public void registerCategory(Category category) {
        categoryRepository.save(category);
    }

    @Override
    public void saveChangesOfCategory(Category category) {
        if (!categoryRepository.existsById(category.getId())) {
            throw new EntityNotFoundException("Category", category.getId());
        }
        categoryRepository.save(category);
    }

    @Override
    public void deleteCategoryById(long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category", id));

        if (!category.getCars().isEmpty()) {
            throw new BusinessException("Cannot delete a category that has cars assigned to it");
        }

        categoryRepository.delete(category);
    }
}
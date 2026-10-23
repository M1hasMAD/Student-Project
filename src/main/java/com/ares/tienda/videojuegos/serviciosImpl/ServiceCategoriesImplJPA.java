package com.ares.tienda.videojuegos.serviciosImpl;

import com.ares.tienda.videojuegos.interfaces.ServiceCategories;
import com.ares.tienda.videojuegos.model.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ServiceCategoriesImplJPA implements ServiceCategories {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Category> getCategories() {
        return entityManager
                .createQuery("select c from Category c", Category.class)
                .getResultList();
    }

    @Override
    public Category getCategoryById(long id) {
        return entityManager.find(Category.class, id);
    }

    @Override
    public void registerCategory(Category category) {
        entityManager.persist(category);
    }

    @Override
    public void saveChangesOfCategory(Category category) {
        entityManager.merge(category);
    }

    @Override
    public void deleteCategoryById(long id) throws Exception {
        Category category = entityManager.find(Category.class, id);

        if (category == null) {
            throw new Exception("Category ID not found");
        }

        if (!category.getCars().isEmpty()) {
            throw new IllegalStateException(
                    "Cannot delete a category that has cars assigned to it"
            );
        }

        entityManager.remove(category);
    }
}
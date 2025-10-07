package com.example.category_service.service;

import com.example.category_service.entity.Category;
import com.example.category_service.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // Create
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    // Read all
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Read by ID
    public Optional<Category> getCategoryById(Integer id) {
        return categoryRepository.findById(id);
    }

    // Update
    public Category updateCategory(Integer id, Category updatedCategory) {
        return categoryRepository.findById(id).map(category -> {
            category.setCategory_name(updatedCategory.getCategory_name());
            category.setCategory_type(updatedCategory.getCategory_type());
            category.setDescription(updatedCategory.getDescription());
            category.setIs_synced(updatedCategory.getIs_synced());
            return categoryRepository.save(category);
        }).orElseThrow(() -> new RuntimeException("Category not found with id " + id));
    }

    // Delete
    public void deleteCategory(Integer id) {
        categoryRepository.deleteById(id);
    }
}

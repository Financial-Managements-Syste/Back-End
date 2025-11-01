package com.example.category_service.service;

import com.example.category_service.entity.sqlite.SQLiteCategory;
import com.example.category_service.repository.sqlite.SQLiteCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private SQLiteCategoryRepository categoryRepository;

    // Create
    public SQLiteCategory createCategory(SQLiteCategory category) {
        return categoryRepository.save(category);
    }

    // Read all
    public List<SQLiteCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Read by ID
    public Optional<SQLiteCategory> getCategoryById(Integer id) {
        return categoryRepository.findById(id);
    }

    // Update
    public SQLiteCategory updateCategory(Integer id, SQLiteCategory updatedCategory) {
        return categoryRepository.findById(id).map(category -> {
            category.setCategoryName(updatedCategory.getCategoryName());
            category.setCategoryType(updatedCategory.getCategoryType());
            category.setDescription(updatedCategory.getDescription());
            category.setIsSynced(updatedCategory.getIsSynced());
            return categoryRepository.save(category);
        }).orElseThrow(() -> new RuntimeException("Category not found with id " + id));
    }

    // Delete
    public void deleteCategory(Integer id) {
        categoryRepository.deleteById(id);
    }
}

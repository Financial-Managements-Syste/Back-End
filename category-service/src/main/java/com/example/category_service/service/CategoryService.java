package com.example.category_service.service;

import com.example.category_service.entity.sqlite.SQLiteCategory;
import com.example.category_service.repository.sqlite.SQLiteCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private SQLiteCategoryRepository categoryRepository;

    // --- CREATE ---
    public SQLiteCategory createCategory(SQLiteCategory category) {
        category.setIsSynced(0);
        category.setSyncStatus("NEW");
        category.setIsDeleted(0);
        return categoryRepository.save(category);
    }

    // --- READ ALL ---
    public List<SQLiteCategory> getAllCategories() {
        return categoryRepository.findAll().stream()
                .filter(cat -> cat.getIsDeleted() == 0)
                .collect(Collectors.toList());
    }

    // --- READ BY ID ---
    public Optional<SQLiteCategory> getCategoryById(Integer id) {
        return categoryRepository.findById(id)
                .filter(cat -> cat.getIsDeleted() == 0);
    }

    // --- UPDATE ---
    public SQLiteCategory updateCategory(Integer id, SQLiteCategory updatedCategory) {
        return categoryRepository.findById(id).map(category -> {
            category.setCategoryName(updatedCategory.getCategoryName());
            category.setCategoryType(updatedCategory.getCategoryType());
            category.setDescription(updatedCategory.getDescription());
            category.setIsSynced(0);
            category.setSyncStatus("UPDATED");
            return categoryRepository.save(category);
        }).orElseThrow(() -> new RuntimeException("Category not found with ID: " + id));
    }

    // --- SOFT DELETE ---
    public void deleteCategory(Integer id) {
        categoryRepository.findById(id).ifPresent(category -> {
            category.setIsDeleted(1);
            category.setIsSynced(0);
            category.setSyncStatus("DELETED");
            categoryRepository.save(category);
        });
    }
}

package com.example.category_service.entity.oracle;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CATEGORIES")
public class OracleCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CATEGORY_ID")
    private Integer categoryId;

    @Column(name = "CATEGORY_NAME", nullable = false, unique = true, length = 50)
    private String categoryName;

    @Column(name = "CATEGORY_TYPE", nullable = false, length = 20)
    private String categoryType;

    @Column(name = "DESCRIPTION", length = 255)
    private String description;

    @Column(name = "CREATED_AT", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    public OracleCategory() {}

    public OracleCategory(String categoryName, String categoryType, String description, LocalDateTime createdAt) {
        this.categoryName = categoryName;
        this.categoryType = categoryType;
        this.description = description;
        this.createdAt = createdAt;
    }

    // --- Getters & Setters ---
    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

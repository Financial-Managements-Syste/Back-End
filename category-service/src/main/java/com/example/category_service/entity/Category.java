package com.example.category_service.entity;


import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer category_id;

    @Column(nullable = false, unique = true)
    private String category_name;

    @Column(nullable = false)
    private String category_type; // Income or Expense

    private String description;

    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime created_at;

    private Integer is_synced = 0;

    // Getters and Setters
    public Integer getCategory_id() { return category_id; }
    public void setCategory_id(Integer category_id) { this.category_id = category_id; }

    public String getCategory_name() { return category_name; }
    public void setCategory_name(String category_name) { this.category_name = category_name; }

    public String getCategory_type() { return category_type; }
    public void setCategory_type(String category_type) { this.category_type = category_type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreated_at() { return created_at; }
    public void setCreated_at(LocalDateTime created_at) { this.created_at = created_at; }

    public Integer getIs_synced() { return is_synced; }
    public void setIs_synced(Integer is_synced) { this.is_synced = is_synced; }
}

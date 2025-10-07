package com.example.savving_service.repository;

import com.example.savving_service.entity.SavingsGoals;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavingRepository extends JpaRepository<SavingsGoals, Integer> {
    List<SavingsGoals> findByUserId(int userId);
}

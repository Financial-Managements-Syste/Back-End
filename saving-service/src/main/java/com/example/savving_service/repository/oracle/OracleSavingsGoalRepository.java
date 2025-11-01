package com.example.savving_service.repository.oracle;

import com.example.savving_service.entity.oracle.OracleSavingsGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OracleSavingsGoalRepository extends JpaRepository<OracleSavingsGoal, Long> {
}

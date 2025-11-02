// ReportService.java
package com.example.finance_reports_service.service;

import com.example.finance_reports_service.model.dto.*;
import com.example.finance_reports_service.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    public List<MonthlyExpenditureDTO> getMonthlyExpenditure(Long userId, int month, int year) {
        return reportRepository.getMonthlyExpenditure(userId, month, year);
    }

    public List<BudgetAdherenceDTO> getBudgetAdherence(Long userId) {
        return reportRepository.getBudgetAdherence(userId);
    }

    public List<SavingsProgressDTO> getSavingsProgress(Long userId) {
        return reportRepository.getSavingsProgress(userId);
    }

    public List<CategoryDistributionDTO> getCategoryDistribution(Long userId,
                                                                 LocalDate startDate,
                                                                 LocalDate endDate) {
        return reportRepository.getCategoryDistribution(userId, startDate, endDate);
    }

    public SavingsForecastDTO getSavingsForecast(Long userId) {
        return reportRepository.getSavingsForecast(userId);
    }
}
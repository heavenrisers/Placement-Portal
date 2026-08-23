package com.placementportal.placementportal.service;

import com.placementportal.placementportal.repository.ApplicationRepository;
import com.placementportal.placementportal.repository.StudentProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        long totalStudents = studentProfileRepository.countAllStudents();
        long totalPlaced = applicationRepository.countSelected();
        double placementPercent = totalStudents > 0 ? (totalPlaced * 100.0 / totalStudents) : 0;

        stats.put("totalStudents", totalStudents);
        stats.put("totalPlaced", totalPlaced);
        stats.put("placementPercent", Math.round(placementPercent * 100.0) / 100.0);

        Double avgPackage = applicationRepository.getAveragePackage();
        Double highestPackage = applicationRepository.getHighestPackage();
        Double lowestPackage = applicationRepository.getLowestPackage();

        stats.put("avgPackage", avgPackage != null ? avgPackage : 0);
        stats.put("highestPackage", highestPackage != null ? highestPackage : 0);
        stats.put("lowestPackage", lowestPackage != null ? lowestPackage : 0);

        // Branch-wise data
        List<Object[]> studentsByBranch = studentProfileRepository.getStudentCountByBranch();
        List<Object[]> placedByBranch = studentProfileRepository.getPlacedCountByBranch();

        Map<String, Long> branchTotals = new HashMap<>();
        for (Object[] row : studentsByBranch) {
            branchTotals.put((String) row[0], (Long) row[1]);
        }

        Map<String, Long> branchPlaced = new HashMap<>();
        for (Object[] row : placedByBranch) {
            branchPlaced.put((String) row[0], (Long) row[1]);
        }

        stats.put("branchLabels", new java.util.ArrayList<>(branchTotals.keySet()));
        stats.put("branchTotals", new java.util.ArrayList<>(branchTotals.values()));

        java.util.List<Long> placedValues = new java.util.ArrayList<>();
        for (String branch : branchTotals.keySet()) {
            placedValues.add(branchPlaced.getOrDefault(branch, 0L));
        }
        stats.put("branchPlaced", placedValues);

        // Status funnel
        List<Object[]> statusCounts = applicationRepository.getStatusCounts();
        Map<String, Long> statusMap = new HashMap<>();
        for (Object[] row : statusCounts) {
            statusMap.put(row[0].toString(), (Long) row[1]);
        }
        stats.put("statusLabels", new java.util.ArrayList<>(statusMap.keySet()));
        stats.put("statusValues", new java.util.ArrayList<>(statusMap.values()));

        // Company-wise offers
        List<Object[]> companyOffers = applicationRepository.getCompanyWiseOffers();
        Map<String, Long> companyMap = new HashMap<>();
        for (Object[] row : companyOffers) {
            companyMap.put((String) row[0], (Long) row[1]);
        }
        stats.put("companyLabels", new java.util.ArrayList<>(companyMap.keySet()));
        stats.put("companyValues", new java.util.ArrayList<>(companyMap.values()));

        return stats;
    }
}
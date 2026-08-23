package com.placementportal.placementportal.repository;

import com.placementportal.placementportal.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByStudentId(Long studentId);

    List<Application> findByJobPostingId(Long jobPostingId);

    Optional<Application> findByStudentIdAndJobPostingId(Long studentId, Long jobPostingId);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.status = 'SELECTED'")
    long countSelected();

    @Query("SELECT AVG(a.jobPosting.packageOffered) FROM Application a WHERE a.status = 'SELECTED'")
    Double getAveragePackage();

    @Query("SELECT MAX(a.jobPosting.packageOffered) FROM Application a WHERE a.status = 'SELECTED'")
    Double getHighestPackage();

    @Query("SELECT MIN(a.jobPosting.packageOffered) FROM Application a WHERE a.status = 'SELECTED'")
    Double getLowestPackage();

    @Query("SELECT a.status, COUNT(a) FROM Application a GROUP BY a.status")
    List<Object[]> getStatusCounts();

    @Query("SELECT jp.recruiter.companyName, COUNT(a) FROM Application a JOIN a.jobPosting jp WHERE a.status = 'SELECTED' GROUP BY jp.recruiter.companyName")
    List<Object[]> getCompanyWiseOffers();
}
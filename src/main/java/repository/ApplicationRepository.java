package com.placementportal.placementportal.repository;

import com.placementportal.placementportal.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByStudentId(Long studentId);
    List<Application> findByJobPostingId(Long jobPostingId);
    Optional<Application> findByStudentIdAndJobPostingId(Long studentId, Long jobPostingId);
}
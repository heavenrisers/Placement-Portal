package com.placementportal.placementportal.repository;

import com.placementportal.placementportal.entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {

    Optional<StudentProfile> findByUserId(Long userId);

    @Query("SELECT COUNT(s) FROM StudentProfile s")
    long countAllStudents();

    @Query("SELECT s.branch, COUNT(s) FROM StudentProfile s GROUP BY s.branch")
    List<Object[]> getStudentCountByBranch();

    @Query("SELECT s.branch, COUNT(DISTINCT a.student) FROM Application a JOIN a.student s WHERE a.status = 'SELECTED' GROUP BY s.branch")
    List<Object[]> getPlacedCountByBranch();
}
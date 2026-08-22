package com.placementportal.placementportal.service;

import com.placementportal.placementportal.entity.JobPosting;
import com.placementportal.placementportal.entity.User;
import com.placementportal.placementportal.repository.JobPostingRepository;
import com.placementportal.placementportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobPostingRepository jobPostingRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<JobPosting> getAllJobPostings() {
        return jobPostingRepository.findAll();
    }

    public void deleteJobPosting(Long jobId) {
        jobPostingRepository.deleteById(jobId);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
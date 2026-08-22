package com.placementportal.placementportal.service;

import com.placementportal.placementportal.dto.JobPostingDTO;
import com.placementportal.placementportal.entity.JobPosting;
import com.placementportal.placementportal.entity.RecruiterProfile;
import com.placementportal.placementportal.repository.JobPostingRepository;
import com.placementportal.placementportal.repository.RecruiterProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.placementportal.placementportal.entity.Application;
import com.placementportal.placementportal.enums.ApplicationStatus;
import com.placementportal.placementportal.repository.ApplicationRepository;

import java.util.List;

@Service
public class RecruiterService {

    @Autowired
    private JobPostingRepository jobPostingRepository;

    @Autowired
    private RecruiterProfileRepository recruiterProfileRepository;

    public RecruiterProfile getRecruiterProfileByUserId(Long userId) {
        return recruiterProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Recruiter profile not found"));
    }

    public List<JobPosting> getMyPostings(Long userId) {
        RecruiterProfile recruiter = getRecruiterProfileByUserId(userId);
        return jobPostingRepository.findByRecruiterId(recruiter.getId());
    }

    public void createJobPosting(Long userId, JobPostingDTO dto) {
        RecruiterProfile recruiter = getRecruiterProfileByUserId(userId);

        JobPosting job = new JobPosting();
        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setMinCgpa(dto.getMinCgpa());
        job.setEligibleBranch(dto.getEligibleBranch());
        job.setBacklogsAllowed(dto.getBacklogsAllowed());
        job.setPackageOffered(dto.getPackageOffered());
        job.setRecruiter(recruiter);

        jobPostingRepository.save(job);
    }

    public JobPosting getJobForEdit(Long jobId, Long userId) {
        JobPosting job = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job posting not found"));

        if (!job.getRecruiter().getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not authorized to edit this job posting");
        }

        return job;
    }

    public void updateJobPosting(Long jobId, Long userId, JobPostingDTO dto) {
        JobPosting job = getJobForEdit(jobId, userId);

        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setMinCgpa(dto.getMinCgpa());
        job.setEligibleBranch(dto.getEligibleBranch());
        job.setBacklogsAllowed(dto.getBacklogsAllowed());
        job.setPackageOffered(dto.getPackageOffered());

        jobPostingRepository.save(job);
    }

    public void deleteJobPosting(Long jobId, Long userId) {
        JobPosting job = getJobForEdit(jobId, userId);
        jobPostingRepository.deleteById(job.getId());
    }

    public void updateCompanyName(Long userId, String companyName) {
        RecruiterProfile recruiter = getRecruiterProfileByUserId(userId);
        recruiter.setCompanyName(companyName);
        recruiterProfileRepository.save(recruiter);
    }

    @Autowired
    private ApplicationRepository applicationRepository;

    public List<Application> getApplicantsForJob(Long jobId, Long userId) {
        JobPosting job = getJobForEdit(jobId, userId); // reuses ownership check
        return applicationRepository.findByJobPostingId(job.getId());
    }

    public void updateApplicationStatus(Long applicationId, Long userId, ApplicationStatus status) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        // Ownership check: does this application belong to a job posted by this recruiter?
        if (!application.getJobPosting().getRecruiter().getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not authorized to update this application");
        }

        application.setStatus(status);
        applicationRepository.save(application);
    }
    public com.placementportal.placementportal.entity.Application getApplicationForScreening(Long applicationId, Long userId) {
        com.placementportal.placementportal.entity.Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (!application.getJobPosting().getRecruiter().getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not authorized to screen this application");
        }

        return application;
    }
}
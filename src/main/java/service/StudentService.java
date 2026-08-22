package com.placementportal.placementportal.service;

import com.placementportal.placementportal.dto.StudentProfileDTO;
import com.placementportal.placementportal.entity.Application;
import com.placementportal.placementportal.entity.JobPosting;
import com.placementportal.placementportal.entity.StudentProfile;
import com.placementportal.placementportal.enums.ApplicationStatus;
import com.placementportal.placementportal.repository.ApplicationRepository;
import com.placementportal.placementportal.repository.JobPostingRepository;
import com.placementportal.placementportal.repository.StudentProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private JobPostingRepository jobPostingRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    public StudentProfile getProfileByUserId(Long userId) {
        return studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));
    }

    public void updateProfile(Long userId, StudentProfileDTO dto, MultipartFile resumeFile) {
        System.out.println("=== DEBUG START ===");
        System.out.println("resumeFile is null? " + (resumeFile == null));
        if (resumeFile != null) {
            System.out.println("resumeFile.isEmpty()? " + resumeFile.isEmpty());
            System.out.println("resumeFile.getOriginalFilename(): " + resumeFile.getOriginalFilename());
            System.out.println("resumeFile.getSize(): " + resumeFile.getSize());
        }
        System.out.println("=== DEBUG END ===");

        StudentProfile profile = getProfileByUserId(userId);
        profile.setBranch(dto.getBranch());
        profile.setCgpa(dto.getCgpa());
        profile.setBacklogs(dto.getBacklogs());

        if (resumeFile != null && !resumeFile.isEmpty()) {
            try {
                String uploadDir = System.getProperty("user.dir") + "/uploads/resumes/";
                java.io.File dir = new java.io.File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                String fileName = "student_" + userId + "_" + resumeFile.getOriginalFilename();
                String filePath = uploadDir + fileName;

                resumeFile.transferTo(new java.io.File(filePath));
                profile.setResumePath(filePath);

                System.out.println("DEBUG - resumePath set to: " + profile.getResumePath());

            } catch (Exception e) {
                throw new RuntimeException("Failed to upload resume: " + e.getMessage());
            }
        }

        studentProfileRepository.save(profile);
        System.out.println("DEBUG - Profile saved. Final resumePath: " + profile.getResumePath());
    }

    public String extractResumeText(String resumePath) {
        try {
            org.apache.pdfbox.pdmodel.PDDocument document = org.apache.pdfbox.Loader.loadPDF(new java.io.File(resumePath));
            org.apache.pdfbox.text.PDFTextStripper stripper = new org.apache.pdfbox.text.PDFTextStripper();
            String text = stripper.getText(document);
            document.close();
            return text;
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract resume text: " + e.getMessage());
        }
    }

    public List<JobPosting> getAllJobPostings() {
        return jobPostingRepository.findAll();
    }

    public boolean isEligible(StudentProfile student, JobPosting job) {
        if (job.getMinCgpa() != null && student.getCgpa() != null
                && student.getCgpa() < job.getMinCgpa()) {
            return false;
        }

        if (job.getEligibleBranch() != null
                && !job.getEligibleBranch().equalsIgnoreCase("ALL")
                && !job.getEligibleBranch().equalsIgnoreCase(student.getBranch())) {
            return false;
        }

        if (Boolean.FALSE.equals(job.getBacklogsAllowed())
                && student.getBacklogs() != null && student.getBacklogs() > 0) {
            return false;
        }

        return true;
    }

    public void applyToJob(Long userId, Long jobId) {
        StudentProfile student = getProfileByUserId(userId);
        JobPosting job = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job posting not found"));

        if (!isEligible(student, job)) {
            throw new RuntimeException("You are not eligible for this job posting");
        }

        if (applicationRepository.findByStudentIdAndJobPostingId(student.getId(), jobId).isPresent()) {
            throw new RuntimeException("You have already applied to this job");
        }

        Application application = new Application();
        application.setStudent(student);
        application.setJobPosting(job);
        application.setStatus(ApplicationStatus.APPLIED);
        applicationRepository.save(application);
    }

    public List<Application> getMyApplications(Long userId) {
        StudentProfile student = getProfileByUserId(userId);
        return applicationRepository.findByStudentId(student.getId());
    }
}
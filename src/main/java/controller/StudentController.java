package com.placementportal.placementportal.controller;

import com.placementportal.placementportal.config.UserDetailsImpl;
import com.placementportal.placementportal.dto.StudentProfileDTO;
import com.placementportal.placementportal.entity.Application;
import com.placementportal.placementportal.entity.JobPosting;
import com.placementportal.placementportal.entity.StudentProfile;
import com.placementportal.placementportal.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/student/profile")
    public String viewProfile(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        Long userId = userDetails.getUser().getId();
        StudentProfile profile = studentService.getProfileByUserId(userId);
        model.addAttribute("profile", profile);
        return "student-profile";
    }

    @GetMapping("/student/profile/edit")
    public String showEditForm(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        Long userId = userDetails.getUser().getId();
        StudentProfile profile = studentService.getProfileByUserId(userId);

        StudentProfileDTO dto = new StudentProfileDTO();
        dto.setBranch(profile.getBranch());
        dto.setCgpa(profile.getCgpa());
        dto.setBacklogs(profile.getBacklogs());

        model.addAttribute("profileDTO", dto);
        return "student-profile-edit";
    }

    @PostMapping("/student/profile/edit")
    public String updateProfile(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                @ModelAttribute StudentProfileDTO profileDTO,
                                @RequestParam(value = "resumeFile", required = false) MultipartFile resumeFile) {
        Long userId = userDetails.getUser().getId();
        studentService.updateProfile(userId, profileDTO, resumeFile);
        return "redirect:/student/profile";
    }

    @GetMapping("/student/jobs")
    public String viewJobs(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        Long userId = userDetails.getUser().getId();
        StudentProfile student = studentService.getProfileByUserId(userId);
        List<JobPosting> jobs = studentService.getAllJobPostings();

        model.addAttribute("jobs", jobs);
        model.addAttribute("student", student);
        return "student-jobs";
    }

    @PostMapping("/student/jobs/{id}/apply")
    public String applyToJob(@PathVariable Long id,
                             @AuthenticationPrincipal UserDetailsImpl userDetails,
                             RedirectAttributes redirectAttributes) {
        Long userId = userDetails.getUser().getId();
        try {
            studentService.applyToJob(userId, id);
            redirectAttributes.addFlashAttribute("message", "Applied successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/student/jobs";
    }


    @GetMapping("/student/applications")
    public String myApplications(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        Long userId = userDetails.getUser().getId();
        List<Application> applications = studentService.getMyApplications(userId);
        model.addAttribute("applications", applications);
        return "student-applications";
    }
}
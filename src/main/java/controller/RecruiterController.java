package com.placementportal.placementportal.controller;

import com.placementportal.placementportal.config.UserDetailsImpl;
import com.placementportal.placementportal.dto.JobPostingDTO;
import com.placementportal.placementportal.entity.JobPosting;
import com.placementportal.placementportal.service.RecruiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/recruiter")
public class RecruiterController {

    @Autowired
    private RecruiterService recruiterService;

    @GetMapping("/postings")
    public String myPostings(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        Long userId = userDetails.getUser().getId();
        List<JobPosting> postings = recruiterService.getMyPostings(userId);
        model.addAttribute("postings", postings);
        return "recruiter-postings";
    }

    @GetMapping("/postings/create")
    public String showCreateForm(Model model) {
        model.addAttribute("jobDTO", new JobPostingDTO());
        return "recruiter-posting-form";
    }

    @PostMapping("/postings/create")
    public String createPosting(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                @ModelAttribute JobPostingDTO jobDTO) {
        Long userId = userDetails.getUser().getId();
        recruiterService.createJobPosting(userId, jobDTO);
        return "redirect:/recruiter/postings";
    }

    @GetMapping("/postings/{id}/edit")
    public String showEditForm(@PathVariable Long id,
                               @AuthenticationPrincipal UserDetailsImpl userDetails,
                               Model model) {
        Long userId = userDetails.getUser().getId();
        JobPosting job = recruiterService.getJobForEdit(id, userId);

        JobPostingDTO dto = new JobPostingDTO();
        dto.setTitle(job.getTitle());
        dto.setDescription(job.getDescription());
        dto.setMinCgpa(job.getMinCgpa());
        dto.setEligibleBranch(job.getEligibleBranch());
        dto.setBacklogsAllowed(job.getBacklogsAllowed());
        dto.setPackageOffered(job.getPackageOffered());

        model.addAttribute("jobDTO", dto);
        model.addAttribute("jobId", id);
        return "recruiter-posting-form";
    }

    @PostMapping("/postings/{id}/edit")
    public String updatePosting(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetailsImpl userDetails,
                                @ModelAttribute JobPostingDTO jobDTO) {
        Long userId = userDetails.getUser().getId();
        recruiterService.updateJobPosting(id, userId, jobDTO);
        return "redirect:/recruiter/postings";
    }

    @PostMapping("/postings/{id}/delete")
    public String deletePosting(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        recruiterService.deleteJobPosting(id, userId);
        return "redirect:/recruiter/postings";
    }

    @GetMapping("/profile")
    public String showProfileForm(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        Long userId = userDetails.getUser().getId();
        com.placementportal.placementportal.entity.RecruiterProfile profile = recruiterService.getRecruiterProfileByUserId(userId);
        model.addAttribute("companyName", profile.getCompanyName());
        return "recruiter-profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                @RequestParam String companyName) {
        Long userId = userDetails.getUser().getId();
        recruiterService.updateCompanyName(userId, companyName);
        return "redirect:/recruiter/dashboard";
    }

    @GetMapping("/postings/{id}/applicants")
    public String viewApplicants(@PathVariable Long id,
                                 @AuthenticationPrincipal UserDetailsImpl userDetails,
                                 Model model) {
        Long userId = userDetails.getUser().getId();
        List<com.placementportal.placementportal.entity.Application> applicants =
                recruiterService.getApplicantsForJob(id, userId);
        model.addAttribute("applicants", applicants);
        model.addAttribute("jobId", id);
        return "recruiter-applicants";
    }

    @PostMapping("/applications/{applicationId}/status")
    public String updateStatus(@PathVariable Long applicationId,
                               @AuthenticationPrincipal UserDetailsImpl userDetails,
                               @RequestParam com.placementportal.placementportal.enums.ApplicationStatus status,
                               @RequestParam Long jobId) {
        Long userId = userDetails.getUser().getId();
        recruiterService.updateApplicationStatus(applicationId, userId, status);
        return "redirect:/recruiter/postings/" + jobId + "/applicants";
    }

    @Autowired
    private com.placementportal.placementportal.service.AiScreeningService aiScreeningService;

    @Autowired
    private com.placementportal.placementportal.service.StudentService studentService;

    @PostMapping("/applications/{applicationId}/ai-screen")
    public String screenApplication(@PathVariable Long applicationId,
                                    @RequestParam Long jobId,
                                    @AuthenticationPrincipal UserDetailsImpl userDetails,
                                    org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

        com.placementportal.placementportal.entity.Application application =
                recruiterService.getApplicationForScreening(applicationId, userDetails.getUser().getId());

        String resumePath = application.getStudent().getResumePath();
        if (resumePath == null) {
            redirectAttributes.addFlashAttribute("aiError", "Student has not uploaded a resume yet.");
            return "redirect:/recruiter/postings/" + jobId + "/applicants";
        }

        String resumeText = studentService.extractResumeText(resumePath);
        String jobTitle = application.getJobPosting().getTitle();
        String jobDescription = application.getJobPosting().getDescription();

        String result = aiScreeningService.screenResume(resumeText, jobTitle, jobDescription);

        redirectAttributes.addFlashAttribute("aiResult", result);
        redirectAttributes.addFlashAttribute("aiApplicationId", applicationId);

        return "redirect:/recruiter/postings/" + jobId + "/applicants";
    }
}

package com.placementportal.placementportal.controller;

import com.placementportal.placementportal.entity.JobPosting;
import com.placementportal.placementportal.entity.User;
import com.placementportal.placementportal.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/users")
    public String viewUsers(Model model) {
        List<User> users = adminService.getAllUsers();
        model.addAttribute("users", users);
        return "admin-users";
    }

    @GetMapping("/postings")
    public String viewPostings(Model model) {
        List<JobPosting> postings = adminService.getAllJobPostings();
        model.addAttribute("postings", postings);
        return "admin-postings";
    }

    @PostMapping("/postings/{id}/delete")
    public String deletePosting(@PathVariable Long id) {
        adminService.deleteJobPosting(id);
        return "redirect:/admin/postings";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return "redirect:/admin/users";
    }
}
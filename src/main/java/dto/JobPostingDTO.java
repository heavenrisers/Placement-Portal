package com.placementportal.placementportal.dto;

import lombok.Data;

@Data
public class JobPostingDTO {
    private String title;
    private String description;
    private Double minCgpa;
    private String eligibleBranch;
    private Boolean backlogsAllowed;
    private Double packageOffered;
}
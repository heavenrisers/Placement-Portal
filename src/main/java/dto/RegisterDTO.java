package com.placementportal.placementportal.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    private String name;
    private String email;
    private String password;
    private String role; // "STUDENT" or "RECRUITER"
}
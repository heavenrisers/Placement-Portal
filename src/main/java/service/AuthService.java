package com.placementportal.placementportal.service;

import com.placementportal.placementportal.dto.RegisterDTO;
import com.placementportal.placementportal.entity.RecruiterProfile;
import com.placementportal.placementportal.entity.StudentProfile;
import com.placementportal.placementportal.entity.User;
import com.placementportal.placementportal.enums.Role;
import com.placementportal.placementportal.repository.RecruiterProfileRepository;
import com.placementportal.placementportal.repository.StudentProfileRepository;
import com.placementportal.placementportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private RecruiterProfileRepository recruiterProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerUser(RegisterDTO dto) {

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.valueOf(dto.getRole()));

        User savedUser = userRepository.save(user);

        if (savedUser.getRole() == Role.STUDENT) {
            StudentProfile profile = new StudentProfile();
            profile.setUser(savedUser);
            studentProfileRepository.save(profile);
        } else if (savedUser.getRole() == Role.RECRUITER) {
            RecruiterProfile profile = new RecruiterProfile();
            profile.setUser(savedUser);
            recruiterProfileRepository.save(profile);
        }
        // ADMIN doesn't need a separate profile for now
    }
}
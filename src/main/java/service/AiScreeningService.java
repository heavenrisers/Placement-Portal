package com.placementportal.placementportal.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class AiScreeningService {

    @Value("${gemini.api.key}")
    private String apiKey;

    public String screenResume(String resumeText, String jobTitle, String jobDescription) {

        String prompt = "You are a recruiter's assistant. Given the following resume text and job description, "
                + "rate the candidate's fit for this role on a scale of 1 to 10, and give a 2-3 sentence explanation. "
                + "Respond in this exact format: 'Score: X/10. Reason: ...'\n\n"
                + "JOB TITLE: " + jobTitle + "\n"
                + "JOB DESCRIPTION: " + jobDescription + "\n\n"
                + "RESUME TEXT:\n" + resumeText;

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent?key=" + apiKey;

        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );

        try {
            Map<String, Object> response = restTemplate.postForObject(url, requestBody, Map.class);

            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            String resultText = (String) parts.get(0).get("text");

            return resultText;

        } catch (Exception e) {
            return "AI screening failed: " + e.getMessage();
        }
    }
}
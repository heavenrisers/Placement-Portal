# Placement Management Portal

A full-stack web application for managing college placement activities, built with a 3-role system (Student, Recruiter, Admin) supporting the complete placement lifecycle — from job posting to AI-assisted candidate screening to final selection, plus real-time placement analytics.

## Tech Stack

- **Backend:** Java 21, Spring Boot 4, Spring Security, Spring Data JPA
- **Database:** MySQL
- **Frontend:** Thymeleaf, Bootstrap 5, Chart.js
- **AI:** Google Gemini API (resume screening)
- **PDF Processing:** Apache PDFBox
- **Build Tool:** Maven

## Features

### Authentication & Authorization
- Role-based registration and login (Student / Recruiter / Admin)
- BCrypt password hashing
- Spring Security role-based access control enforced at both the URL level and the data-ownership level

### Student
- Profile management (branch, CGPA, backlogs)
- Resume upload (PDF) with server-side storage
- Browse job postings and apply, with server-side eligibility validation (CGPA, branch, backlog policy)
- Track application status (Applied → Shortlisted → Selected/Rejected)

### Recruiter
- Company profile management
- Full CRUD for job postings, with ownership-based authorization
- View applicants per posting and update their status
- **AI-powered resume screening** — extracts text from a candidate's uploaded PDF resume and sends it, along with the job description, to Google's Gemini API to generate a fit score and reasoning

### Admin
- View and manage all users and job postings
- **Placement analytics dashboard**: total students, placement %, average/highest/lowest package, branch-wise placement breakdown, application status funnel, and company-wise offer counts — visualized with Chart.js

## Key Technical Details

- **Eligibility engine:** applications are validated server-side against CGPA, branch, and backlog criteria before being accepted
- **Ownership authorization:** recruiters can only manage postings and screen applicants for jobs they created; verified via manual penetration testing (confirmed 403 responses on cross-role access attempts)
- **AI integration:** PDF resumes are parsed with Apache PDFBox and evaluated by the Gemini API, with results displayed inline for recruiters
- **Analytics via aggregation:** custom JPQL queries (COUNT, AVG, GROUP BY) power the admin dashboard without any additional data duplication
- **Secrets management:** API keys and database credentials are kept out of version control using Spring profiles (`application-local.properties`, gitignored)

## Setup Instructions

1. Clone the repository
2. Create a MySQL database named `placement_portal`
3. Create `src/main/resources/application-local.properties` with your own values:
```properties
   spring.datasource.password=your_mysql_password
gemini.api.key=your_gemini_api_key
```
4. Get a free Gemini API key at [aistudio.google.com](https://aistudio.google.com)
5. Run with the `local` Spring profile active
6. Visit `http://localhost:8080/register` to create an account
# Placement Management Portal

A full-stack web application for managing college placement activities, built with a 3-role system (Student, Recruiter, Admin) supporting the complete placement lifecycle from job posting to selection.

## Tech Stack

- **Backend:** Java 21, Spring Boot 4, Spring Security, Spring Data JPA
- **Database:** MySQL
- **Frontend:** Thymeleaf, HTML/CSS
- **Build Tool:** Maven

## Features

### Authentication & Authorization
- Role-based registration and login (Student / Recruiter / Admin)
- BCrypt password hashing
- Spring Security role-based access control at both the URL level and the data (ownership) level

### Student
- Profile management (branch, CGPA, backlogs, resume link)
- Browse all job postings
- Apply to jobs with server-side eligibility validation (CGPA, branch, backlog policy)
- Track application status (Applied → Shortlisted → Selected/Rejected)

### Recruiter
- Company profile management
- Create, edit, delete job postings
- View applicants per posting
- Update application status
- Ownership checks ensure recruiters can only manage their own postings

### Admin
- View all registered users
- View all job postings across recruiters
- Delete users/postings for moderation

## Key Technical Details

- **Eligibility engine:** applications are validated server-side against CGPA, branch, and backlog criteria before being accepted — not just UI-level restriction
- **Ownership authorization:** beyond role checks, the app verifies that a recruiter can only modify job postings they created, and only they can update statuses for applicants to their own postings
- **Relational schema:** 6 entities (User, StudentProfile, RecruiterProfile, JobPosting, Application) with proper one-to-one and one-to-many relationships

## Setup Instructions

1. Clone the repository
2. Create a MySQL database named `placement_portal`
3. Update `src/main/resources/application.properties` with your MySQL credentials
4. Run `PlacementPortalApplication.java`
5. Visit `http://localhost:8080/register` to create an account

## Future Enhancements

- Admin analytics dashboard (placement statistics, branch-wise reports)
- Resume file upload (currently uses URL-based resume links)
- Email notifications on status changes
package com.example.quanlydaotao.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "student_course_sections")
public class StudentCourseSection extends AuditedEntity {

    @Column(name = "student_id")
    private UUID studentId;

    @Column(name = "course_section_id")
    private UUID courseSectionId;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @Column(name = "registration_type", length = 100)
    private String registrationType;

    @Column(name = "status", length = 100)
    private String status;

    @Column(name = "attempt_number", length = 100)
    private String attemptNumber;

    @Column(name = "note", length = 255)
    private String note;

    public UUID getStudentId() { return studentId; }
    public void setStudentId(UUID studentId) { this.studentId = studentId; }
    public UUID getCourseSectionId() { return courseSectionId; }
    public void setCourseSectionId(UUID courseSectionId) { this.courseSectionId = courseSectionId; }
    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDateTime registrationDate) { this.registrationDate = registrationDate; }
    public String getRegistrationType() { return registrationType; }
    public void setRegistrationType(String registrationType) { this.registrationType = registrationType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAttemptNumber() { return attemptNumber; }
    public void setAttemptNumber(String attemptNumber) { this.attemptNumber = attemptNumber; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}

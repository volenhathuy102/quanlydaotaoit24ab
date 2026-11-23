
package com.example.quanlydaotao.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    // Mã sinh viên
    @Column(name = "code")
    private String code;

    // Họ và tên
    @Column(name = "full_name")
    private String fullName;

    // Email - cột vừa bổ sung trong database
    @Column(name = "email")
    private String email;

    // Số điện thoại - cột vừa bổ sung trong database
    @Column(name = "phone")
    private String phone;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "gender")
    private String gender;

    @Column(name = "personal_identification_number")
    private String personalIdentificationNumber;

    @Column(name = "date_of_issue")
    private LocalDate dateOfIssue;

    @Column(name = "card_place")
    private String cardPlace;

    @Column(name = "address")
    private String address;

    @Column(name = "current_address")
    private String currentAddress;

    @Column(name = "academic_year_year")
    private LocalDateTime academicYearYear;

    @Column(name = "department_id")
    private UUID departmentId;

    @Column(name = "major_id")
    private UUID majorId;

    @Column(name = "training_program_id")
    private UUID trainingProgramId;

    @Column(name = "status_id")
    private UUID statusId;

    // ID lớp, không phải tên lớp
    @Column(name = "student_classess_id")
    private UUID studentClassessId;

    @Column(name = "admission_year")
    private LocalDateTime admissionYear;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private UUID deletedBy;

    @Column(name = "is_active")
    private Boolean isActive;

    public Student() {
    }

    // ID
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    // User ID
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    // Mã sinh viên
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    // Họ và tên
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // Tương thích với code cũ dùng studentCode
    public String getStudentCode() {
        return code;
    }

    public void setStudentCode(String studentCode) {
        this.code = studentCode;
    }

    // Tương thích với code cũ dùng name
    public String getName() {
        return fullName;
    }

    public void setName(String name) {
        this.fullName = name;
    }

    // Email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Số điện thoại
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // Ngày sinh
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    // Giới tính
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    // Số CCCD
    public String getPersonalIdentificationNumber() {
        return personalIdentificationNumber;
    }

    public void setPersonalIdentificationNumber(
            String personalIdentificationNumber) {
        this.personalIdentificationNumber = personalIdentificationNumber;
    }

    // Ngày cấp CCCD
    public LocalDate getDateOfIssue() {
        return dateOfIssue;
    }

    public void setDateOfIssue(LocalDate dateOfIssue) {
        this.dateOfIssue = dateOfIssue;
    }

    // Nơi cấp CCCD
    public String getCardPlace() {
        return cardPlace;
    }

    public void setCardPlace(String cardPlace) {
        this.cardPlace = cardPlace;
    }

    // Địa chỉ
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // Địa chỉ hiện tại
    public String getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

    public LocalDateTime getAcademicYearYear() {
        return academicYearYear;
    }

    public void setAcademicYearYear(LocalDateTime academicYearYear) {
        this.academicYearYear = academicYearYear;
    }

    public UUID getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(UUID departmentId) {
        this.departmentId = departmentId;
    }

    public UUID getMajorId() {
        return majorId;
    }

    public void setMajorId(UUID majorId) {
        this.majorId = majorId;
    }

    public UUID getTrainingProgramId() {
        return trainingProgramId;
    }

    public void setTrainingProgramId(UUID trainingProgramId) {
        this.trainingProgramId = trainingProgramId;
    }

    public UUID getStatusId() {
        return statusId;
    }

    public void setStatusId(UUID statusId) {
        this.statusId = statusId;
    }

    // Lớp
    public UUID getStudentClassessId() {
        return studentClassessId;
    }

    public void setStudentClassessId(UUID studentClassessId) {
        this.studentClassessId = studentClassessId;
    }

    public LocalDateTime getAdmissionYear() {
        return admissionYear;
    }

    public void setAdmissionYear(LocalDateTime admissionYear) {
        this.admissionYear = admissionYear;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public UUID getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(UUID updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public UUID getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(UUID deletedBy) {
        this.deletedBy = deletedBy;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}

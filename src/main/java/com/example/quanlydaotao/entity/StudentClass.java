package com.example.quanlydaotao.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "student_classes")
public class StudentClass extends AuditedEntity {

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "academic_year_id")
    private UUID academicYearId;

    @Column(name = "department_id")
    private UUID departmentId;

    @Column(name = "major_id")
    private UUID majorId;

    @Column(name = "training_program_id")
    private UUID trainingProgramId;

    @Column(name = "advisor_id")
    private UUID advisorId;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public UUID getAcademicYearId() { return academicYearId; }
    public void setAcademicYearId(UUID academicYearId) { this.academicYearId = academicYearId; }
    public UUID getDepartmentId() { return departmentId; }
    public void setDepartmentId(UUID departmentId) { this.departmentId = departmentId; }
    public UUID getMajorId() { return majorId; }
    public void setMajorId(UUID majorId) { this.majorId = majorId; }
    public UUID getTrainingProgramId() { return trainingProgramId; }
    public void setTrainingProgramId(UUID trainingProgramId) { this.trainingProgramId = trainingProgramId; }
    public UUID getAdvisorId() { return advisorId; }
    public void setAdvisorId(UUID advisorId) { this.advisorId = advisorId; }
}

package com.example.quanlydaotao.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "courses")
public class Course extends AuditedEntity {

    @Column(name = "department_id")
    private UUID departmentId;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "name_en")
    private String nameEn;

    @Column(name = "credits")
    private java.math.BigDecimal credits;

    @Column(name = "cource_type")
    private String courseType;

    @Column(name = "description")
    private String description;

    public UUID getDepartmentId() { return departmentId; }
    public void setDepartmentId(UUID departmentId) { this.departmentId = departmentId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getNameEn() { return nameEn; }
    public void setNameEn(String nameEn) { this.nameEn = nameEn; }
    public java.math.BigDecimal getCredits() { return credits; }
    public void setCredits(java.math.BigDecimal credits) { this.credits = credits; }
    public String getCourseType() { return courseType; }
    public void setCourseType(String courseType) { this.courseType = courseType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

package com.example.quanlydaotao.entity;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "semesters")
public class Semester extends AuditedEntity {

    @Column(name = "code", length = 100)
    private String code;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "school_year_id")
    private UUID schoolYearId;

    @Column(name = "school_year_name", length = 20)
    private String schoolYearName;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public UUID getSchoolYearId() { return schoolYearId; }
    public void setSchoolYearId(UUID schoolYearId) { this.schoolYearId = schoolYearId; }
    public String getSchoolYearName() { return schoolYearName; }
    public void setSchoolYearName(String schoolYearName) { this.schoolYearName = schoolYearName; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}

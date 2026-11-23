
package com.example.quanlydaotao.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "lecturer_course_classes")
public class LecturerCourseClass extends AuditedEntity {

    @Column(name = "lecturer_id")
    private UUID lecturerId;

    @Column(name = "course_class_id")
    private UUID courseClassId;

    @Column(name = "role", length = 50)
    private String role;

    public UUID getLecturerId() {
        return lecturerId;
    }

    public void setLecturerId(UUID lecturerId) {
        this.lecturerId = lecturerId;
    }

    public UUID getCourseClassId() {
        return courseClassId;
    }

    public void setCourseClassId(UUID courseClassId) {
        this.courseClassId = courseClassId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

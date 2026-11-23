package com.example.quanlydaotao.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.quanlydaotao.entity.StudentCourseSection;

public interface StudentCourseSectionRepository extends SoftDeleteRepository<StudentCourseSection> {

    @Query("""
        SELECT CASE WHEN COUNT(registration) > 0 THEN true ELSE false END
        FROM StudentCourseSection registration
        WHERE registration.studentId = :studentId
          AND registration.courseSectionId = :courseSectionId
          AND registration.deletedAt IS NULL
          AND registration.isActive = true
          AND LOWER(COALESCE(registration.status, '')) NOT IN ('cancelled', 'dropped')
          AND (:id IS NULL OR registration.id <> :id)
        """)
    boolean existsActiveRegistration(
            @Param("studentId") UUID studentId,
            @Param("courseSectionId") UUID courseSectionId,
            @Param("id") UUID id);

    @Query("""
        SELECT COUNT(registration)
        FROM StudentCourseSection registration
        WHERE registration.courseSectionId = :courseSectionId
          AND registration.deletedAt IS NULL
          AND registration.isActive = true
          AND LOWER(COALESCE(registration.status, '')) NOT IN ('cancelled', 'dropped')
        """)
    long countActiveByCourseSectionId(@Param("courseSectionId") UUID courseSectionId);
}

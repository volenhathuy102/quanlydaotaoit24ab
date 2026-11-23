package com.example.quanlydaotao.repository;

import java.util.UUID;

import com.example.quanlydaotao.entity.LecturerCourseClass;

public interface LecturerCourseClassRepository extends SoftDeleteRepository<LecturerCourseClass> {

    boolean existsByLecturerIdAndCourseClassIdAndIsActiveTrueAndDeletedAtIsNull(
            UUID lecturerId, UUID courseClassId);

    boolean existsByLecturerIdAndCourseClassIdAndIsActiveTrueAndDeletedAtIsNullAndIdNot(
            UUID lecturerId, UUID courseClassId, UUID id);
}

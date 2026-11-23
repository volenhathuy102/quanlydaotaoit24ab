package com.example.quanlydaotao.repository;

import java.util.UUID;

import com.example.quanlydaotao.entity.StudentClass;

public interface StudentClassRepository extends SoftDeleteRepository<StudentClass> {
    boolean existsByCodeIgnoreCase(String code);
    boolean existsByCodeIgnoreCaseAndIdNot(String code, UUID id);
}

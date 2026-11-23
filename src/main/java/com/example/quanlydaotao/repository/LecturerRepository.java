package com.example.quanlydaotao.repository;

import java.util.UUID;

import com.example.quanlydaotao.entity.Lecturer;

public interface LecturerRepository extends SoftDeleteRepository<Lecturer> {
    boolean existsByLecturerCodeIgnoreCase(String code);
    boolean existsByLecturerCodeIgnoreCaseAndIdNot(String code, UUID id);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, UUID id);
}

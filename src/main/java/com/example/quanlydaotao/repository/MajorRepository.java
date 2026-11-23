package com.example.quanlydaotao.repository;

import java.util.UUID;

import com.example.quanlydaotao.entity.Major;

public interface MajorRepository extends SoftDeleteRepository<Major> {
    boolean existsByCodeIgnoreCase(String code);
    boolean existsByCodeIgnoreCaseAndIdNot(String code, UUID id);
}

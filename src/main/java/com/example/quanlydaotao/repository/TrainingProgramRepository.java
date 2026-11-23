package com.example.quanlydaotao.repository;

import java.util.UUID;

import com.example.quanlydaotao.entity.TrainingProgram;

public interface TrainingProgramRepository extends SoftDeleteRepository<TrainingProgram> {
    boolean existsByCodeIgnoreCase(String code);
    boolean existsByCodeIgnoreCaseAndIdNot(String code, UUID id);
}

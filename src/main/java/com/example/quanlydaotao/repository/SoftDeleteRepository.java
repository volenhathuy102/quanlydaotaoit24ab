package com.example.quanlydaotao.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.example.quanlydaotao.entity.AuditedEntity;

@NoRepositoryBean
public interface SoftDeleteRepository<T extends AuditedEntity> extends JpaRepository<T, UUID> {

    List<T> findAllByDeletedAtIsNull();

    Optional<T> findByIdAndDeletedAtIsNull(UUID id);
}

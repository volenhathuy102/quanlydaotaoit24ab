package com.example.quanlydaotao.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.quanlydaotao.entity.AuditedEntity;
import com.example.quanlydaotao.repository.SoftDeleteRepository;

public abstract class AbstractCrudService<T extends AuditedEntity> {

    private final SoftDeleteRepository<T> repository;

    protected AbstractCrudService(SoftDeleteRepository<T> repository) {
        this.repository = repository;
    }

    public List<T> list() {
        return repository.findAllByDeletedAtIsNull();
    }

    public T get(UUID id) {
        return repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy dữ liệu"));
    }

    public T create(T entity) {
        entity.setId(null);
        entity.setCreatedAt(null);
        entity.setUpdatedAt(null);
        entity.setDeletedAt(null);
        entity.setDeletedBy(null);
        return repository.save(entity);
    }

    public T update(UUID id, T entity) {
        T current = get(id);
        entity.setId(id);
        entity.setCreatedAt(current.getCreatedAt());
        entity.setCreatedBy(current.getCreatedBy());
        entity.setDeletedAt(null);
        entity.setDeletedBy(null);
        return repository.save(entity);
    }

    public void delete(UUID id) {
        T entity = get(id);
        entity.setDeletedAt(LocalDateTime.now());
        entity.setIsActive(false);
        repository.save(entity);
    }
}

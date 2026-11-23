package com.example.quanlydaotao.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import com.example.quanlydaotao.entity.AuditedEntity;
import com.example.quanlydaotao.service.AbstractCrudService;

public abstract class AbstractCrudController<T extends AuditedEntity> {

    private final AbstractCrudService<T> service;

    protected AbstractCrudController(AbstractCrudService<T> service) {
        this.service = service;
    }

    @GetMapping
    public List<T> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public T get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PostMapping
    public ResponseEntity<T> create(@RequestBody T entity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(entity));
    }

    @PutMapping("/{id}")
    public T update(@PathVariable UUID id, @RequestBody T entity) {
        return service.update(id, entity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

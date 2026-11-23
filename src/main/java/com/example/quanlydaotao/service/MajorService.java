package com.example.quanlydaotao.service;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.quanlydaotao.entity.Major;
import com.example.quanlydaotao.repository.DepartmentRepository;
import com.example.quanlydaotao.repository.MajorRepository;

@Service
public class MajorService extends AbstractCrudService<Major> {

    private final MajorRepository repository;
    private final DepartmentRepository departmentRepository;

    public MajorService(MajorRepository repository, DepartmentRepository departmentRepository) {
        super(repository);
        this.repository = repository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    public Major create(Major major) {
        validate(major, null);
        return super.create(major);
    }

    @Override
    public Major update(UUID id, Major major) {
        validate(major, id);
        return super.update(id, major);
    }

    private void validate(Major major, UUID currentId) {
        major.setCode(clean(major.getCode()));
        major.setName(clean(major.getName()));
        if (major.getCode() == null || major.getName() == null || major.getDepartmentId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã, tên ngành và khoa là bắt buộc");
        }
        departmentRepository.findByIdAndDeletedAtIsNull(major.getDepartmentId())
                .filter(department -> !Boolean.FALSE.equals(department.getIsActive()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Khoa không tồn tại hoặc đã ngừng hoạt động"));
        boolean duplicate = currentId == null
                ? repository.existsByCodeIgnoreCase(major.getCode())
                : repository.existsByCodeIgnoreCaseAndIdNot(major.getCode(), currentId);
        if (duplicate) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Mã ngành đã tồn tại");
        }
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}

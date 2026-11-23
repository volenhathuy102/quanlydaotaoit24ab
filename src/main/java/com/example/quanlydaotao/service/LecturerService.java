package com.example.quanlydaotao.service;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.quanlydaotao.entity.Lecturer;
import com.example.quanlydaotao.repository.DepartmentRepository;
import com.example.quanlydaotao.repository.LecturerRepository;

@Service
public class LecturerService extends AbstractCrudService<Lecturer> {

    private final LecturerRepository repository;
    private final DepartmentRepository departmentRepository;

    public LecturerService(LecturerRepository repository, DepartmentRepository departmentRepository) {
        super(repository);
        this.repository = repository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    public Lecturer create(Lecturer lecturer) {
        validate(lecturer, null);
        return super.create(lecturer);
    }

    @Override
    public Lecturer update(UUID id, Lecturer lecturer) {
        validate(lecturer, id);
        return super.update(id, lecturer);
    }

    private void validate(Lecturer lecturer, UUID currentId) {
        lecturer.setLecturerCode(clean(lecturer.getLecturerCode()));
        lecturer.setFullName(clean(lecturer.getFullName()));
        lecturer.setEmail(clean(lecturer.getEmail()));
        lecturer.setPhone(clean(lecturer.getPhone()));
        if (lecturer.getLecturerCode() == null || lecturer.getFullName() == null
                || lecturer.getDepartmentId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Mã, họ tên và khoa của giảng viên là bắt buộc");
        }
        departmentRepository.findByIdAndDeletedAtIsNull(lecturer.getDepartmentId())
                .filter(item -> !Boolean.FALSE.equals(item.getIsActive()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Khoa không tồn tại hoặc đã ngừng hoạt động"));
        boolean duplicateCode = currentId == null
                ? repository.existsByLecturerCodeIgnoreCase(lecturer.getLecturerCode())
                : repository.existsByLecturerCodeIgnoreCaseAndIdNot(lecturer.getLecturerCode(), currentId);
        if (duplicateCode) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Mã giảng viên đã tồn tại");
        }
        if (lecturer.getEmail() != null) {
            if (!lecturer.getEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email giảng viên không hợp lệ");
            }
            boolean duplicateEmail = currentId == null
                    ? repository.existsByEmailIgnoreCase(lecturer.getEmail())
                    : repository.existsByEmailIgnoreCaseAndIdNot(lecturer.getEmail(), currentId);
            if (duplicateEmail) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email giảng viên đã được sử dụng");
            }
        }
        if (lecturer.getPhone() != null && !lecturer.getPhone().matches("^[+0-9() .-]{7,20}$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số điện thoại giảng viên không hợp lệ");
        }
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}

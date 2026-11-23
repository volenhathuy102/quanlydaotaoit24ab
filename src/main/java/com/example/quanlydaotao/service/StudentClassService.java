package com.example.quanlydaotao.service;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.quanlydaotao.entity.Major;
import com.example.quanlydaotao.entity.StudentClass;
import com.example.quanlydaotao.entity.TrainingProgram;
import com.example.quanlydaotao.repository.AcademicYearRepository;
import com.example.quanlydaotao.repository.DepartmentRepository;
import com.example.quanlydaotao.repository.LecturerRepository;
import com.example.quanlydaotao.repository.MajorRepository;
import com.example.quanlydaotao.repository.StudentClassRepository;
import com.example.quanlydaotao.repository.TrainingProgramRepository;

@Service
public class StudentClassService extends AbstractCrudService<StudentClass> {

    private final StudentClassRepository repository;
    private final AcademicYearRepository academicYearRepository;
    private final DepartmentRepository departmentRepository;
    private final MajorRepository majorRepository;
    private final TrainingProgramRepository programRepository;
    private final LecturerRepository lecturerRepository;

    public StudentClassService(
            StudentClassRepository repository,
            AcademicYearRepository academicYearRepository,
            DepartmentRepository departmentRepository,
            MajorRepository majorRepository,
            TrainingProgramRepository programRepository,
            LecturerRepository lecturerRepository) {
        super(repository);
        this.repository = repository;
        this.academicYearRepository = academicYearRepository;
        this.departmentRepository = departmentRepository;
        this.majorRepository = majorRepository;
        this.programRepository = programRepository;
        this.lecturerRepository = lecturerRepository;
    }

    @Override
    public StudentClass create(StudentClass studentClass) {
        validate(studentClass, null);
        return super.create(studentClass);
    }

    @Override
    public StudentClass update(UUID id, StudentClass studentClass) {
        validate(studentClass, id);
        return super.update(id, studentClass);
    }

    private void validate(StudentClass studentClass, UUID currentId) {
        studentClass.setCode(clean(studentClass.getCode()));
        studentClass.setName(clean(studentClass.getName()));
        if (studentClass.getCode() == null || studentClass.getName() == null
                || studentClass.getAcademicYearId() == null || studentClass.getDepartmentId() == null
                || studentClass.getMajorId() == null || studentClass.getTrainingProgramId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Mã, tên lớp, niên khóa, khoa, ngành và chương trình là bắt buộc");
        }
        academicYearRepository.findByIdAndDeletedAtIsNull(studentClass.getAcademicYearId())
                .filter(item -> !Boolean.FALSE.equals(item.getIsActive()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Niên khóa không tồn tại hoặc đã ngừng hoạt động"));
        departmentRepository.findByIdAndDeletedAtIsNull(studentClass.getDepartmentId())
                .filter(item -> !Boolean.FALSE.equals(item.getIsActive()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Khoa không tồn tại hoặc đã ngừng hoạt động"));
        Major major = majorRepository.findByIdAndDeletedAtIsNull(studentClass.getMajorId())
                .filter(item -> !Boolean.FALSE.equals(item.getIsActive()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ngành không tồn tại hoặc đã ngừng hoạt động"));
        TrainingProgram program = programRepository.findByIdAndDeletedAtIsNull(studentClass.getTrainingProgramId())
                .filter(item -> !Boolean.FALSE.equals(item.getIsActive()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chương trình không tồn tại hoặc đã ngừng hoạt động"));
        if (!studentClass.getDepartmentId().equals(major.getDepartmentId())
                || !studentClass.getDepartmentId().equals(program.getDepartmentId())
                || !studentClass.getMajorId().equals(program.getMajorId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Khoa, ngành và chương trình đào tạo không khớp");
        }
        if (studentClass.getAdvisorId() != null) {
            lecturerRepository.findByIdAndDeletedAtIsNull(studentClass.getAdvisorId())
                    .filter(item -> !Boolean.FALSE.equals(item.getIsActive()))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giảng viên cố vấn không tồn tại hoặc đã ngừng hoạt động"));
        }
        boolean duplicate = currentId == null
                ? repository.existsByCodeIgnoreCase(studentClass.getCode())
                : repository.existsByCodeIgnoreCaseAndIdNot(studentClass.getCode(), currentId);
        if (duplicate) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Mã lớp sinh viên đã tồn tại");
        }
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}

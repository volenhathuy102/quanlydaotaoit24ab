package com.example.quanlydaotao.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.quanlydaotao.entity.CourseSection;
import com.example.quanlydaotao.entity.Student;
import com.example.quanlydaotao.entity.StudentCourseSection;
import com.example.quanlydaotao.repository.CourseSectionRepository;
import com.example.quanlydaotao.repository.StudentRepository;
import com.example.quanlydaotao.repository.StudentCourseSectionRepository;

@Service
public class StudentCourseSectionService extends AbstractCrudService<StudentCourseSection> {

    private final StudentCourseSectionRepository registrationRepository;
    private final StudentRepository studentRepository;
    private final CourseSectionRepository courseSectionRepository;

    public StudentCourseSectionService(
            StudentCourseSectionRepository repository,
            StudentRepository studentRepository,
            CourseSectionRepository courseSectionRepository) {
        super(repository);
        this.registrationRepository = repository;
        this.studentRepository = studentRepository;
        this.courseSectionRepository = courseSectionRepository;
    }

    @Override
    @Transactional
    public StudentCourseSection create(StudentCourseSection registration) {
        return save(registration, null);
    }

    @Override
    @Transactional
    public StudentCourseSection update(UUID id, StudentCourseSection registration) {
        return save(registration, id);
    }

    private StudentCourseSection save(StudentCourseSection registration, UUID currentId) {
        if (registration.getStudentId() == null || registration.getCourseSectionId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Cần chọn sinh viên và lớp học phần");
        }
        Student student = studentRepository.findById(registration.getStudentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sinh viên không tồn tại"));
        if (student.getDeletedAt() != null || Boolean.FALSE.equals(student.getIsActive())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sinh viên đã ngừng hoạt động");
        }
        CourseSection section = courseSectionRepository.findByIdAndDeletedAtIsNull(registration.getCourseSectionId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Lớp học phần không tồn tại hoặc đã bị xóa"));
        boolean cancelled = registration.getStatus() != null
                && ("cancelled".equalsIgnoreCase(registration.getStatus())
                || "dropped".equalsIgnoreCase(registration.getStatus()));
        if (!cancelled && (Boolean.FALSE.equals(section.getIsActive())
                || !"open".equalsIgnoreCase(section.getStatus()))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Lớp học phần hiện không mở đăng ký");
        }

        LocalDateTime now = LocalDateTime.now();
        if (!cancelled && ((section.getRegistrationStart() != null && now.isBefore(section.getRegistrationStart()))
                || (section.getRegistrationEnd() != null && now.isAfter(section.getRegistrationEnd())))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Đã ngoài thời gian đăng ký lớp học phần");
        }

        if (!cancelled) {
            if (registrationRepository.existsActiveRegistration(
                    registration.getStudentId(), registration.getCourseSectionId(), currentId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Sinh viên đã đăng ký lớp học phần này");
            }
            if (section.getMaxStudents() != null
                    && registrationRepository.countActiveByCourseSectionId(section.getId()) >= section.getMaxStudents()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Lớp học phần đã đủ sức chứa");
            }
            if (registration.getStatus() == null || registration.getStatus().isBlank()) {
                registration.setStatus("REGISTERED");
            }
        }
        if (registration.getRegistrationDate() == null) {
            registration.setRegistrationDate(now);
        }
        return currentId == null ? super.create(registration) : super.update(currentId, registration);
    }
}

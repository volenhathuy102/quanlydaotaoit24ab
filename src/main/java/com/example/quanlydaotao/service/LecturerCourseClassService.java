package com.example.quanlydaotao.service;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.quanlydaotao.entity.LecturerCourseClass;
import com.example.quanlydaotao.repository.CourseSectionRepository;
import com.example.quanlydaotao.repository.LecturerRepository;
import com.example.quanlydaotao.repository.LecturerCourseClassRepository;

@Service
public class LecturerCourseClassService extends AbstractCrudService<LecturerCourseClass> {

    private final LecturerCourseClassRepository repository;
    private final LecturerRepository lecturerRepository;
    private final CourseSectionRepository courseSectionRepository;

    public LecturerCourseClassService(
            LecturerCourseClassRepository repository,
            LecturerRepository lecturerRepository,
            CourseSectionRepository courseSectionRepository) {
        super(repository);
        this.repository = repository;
        this.lecturerRepository = lecturerRepository;
        this.courseSectionRepository = courseSectionRepository;
    }

    @Override
    public LecturerCourseClass create(LecturerCourseClass assignment) {
        validate(assignment, null);
        return super.create(assignment);
    }

    @Override
    public LecturerCourseClass update(UUID id, LecturerCourseClass assignment) {
        validate(assignment, id);
        return super.update(id, assignment);
    }

    private void validate(LecturerCourseClass assignment, UUID currentId) {
        if (assignment.getLecturerId() == null || assignment.getCourseClassId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Cần chọn giảng viên và lớp học phần");
        }
        lecturerRepository.findByIdAndDeletedAtIsNull(assignment.getLecturerId())
                .filter(lecturer -> !Boolean.FALSE.equals(lecturer.getIsActive()))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Giảng viên không tồn tại hoặc đã ngừng hoạt động"));
        courseSectionRepository.findByIdAndDeletedAtIsNull(assignment.getCourseClassId())
                .filter(section -> !Boolean.FALSE.equals(section.getIsActive()))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Lớp học phần không tồn tại hoặc đã ngừng hoạt động"));
        boolean duplicate = currentId == null
                ? repository.existsByLecturerIdAndCourseClassIdAndIsActiveTrueAndDeletedAtIsNull(
                        assignment.getLecturerId(), assignment.getCourseClassId())
                : repository.existsByLecturerIdAndCourseClassIdAndIsActiveTrueAndDeletedAtIsNullAndIdNot(
                        assignment.getLecturerId(), assignment.getCourseClassId(), currentId);
        if (duplicate) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Giảng viên đã được phân công vào lớp học phần này");
        }
    }
}

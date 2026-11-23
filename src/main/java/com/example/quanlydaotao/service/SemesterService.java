package com.example.quanlydaotao.service;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.quanlydaotao.entity.Semester;
import com.example.quanlydaotao.entity.SchoolYear;
import com.example.quanlydaotao.repository.SemesterRepository;
import com.example.quanlydaotao.repository.SchoolYearRepository;

@Service
public class SemesterService extends AbstractCrudService<Semester> {

    private final SchoolYearRepository schoolYearRepository;

    public SemesterService(SemesterRepository repository, SchoolYearRepository schoolYearRepository) {
        super(repository);
        this.schoolYearRepository = schoolYearRepository;
    }

    @Override
    public Semester create(Semester semester) {
        validate(semester);
        return super.create(semester);
    }

    @Override
    public Semester update(UUID id, Semester semester) {
        validate(semester);
        return super.update(id, semester);
    }

    private void validate(Semester semester) {
        if (semester.getStartDate() != null && semester.getEndDate() != null
                && semester.getStartDate().isAfter(semester.getEndDate())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Ngày bắt đầu học kỳ phải trước hoặc bằng ngày kết thúc");
        }
        if (semester.getSchoolYearId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vui lòng chọn năm học cho học kỳ");
        }
        SchoolYear schoolYear = schoolYearRepository.findByIdAndDeletedAtIsNull(semester.getSchoolYearId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Năm học được chọn không tồn tại hoặc đã ngừng hoạt động"));
        if (Boolean.FALSE.equals(schoolYear.getIsActive())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không thể gắn học kỳ với năm học đã ngừng hoạt động");
        }
        semester.setSchoolYearName(schoolYear.getName() == null ? schoolYear.getCode() : schoolYear.getName());
        if (schoolYear.getStartDate() != null && semester.getStartDate() != null
                && semester.getStartDate().isBefore(schoolYear.getStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Học kỳ không thể bắt đầu trước năm học");
        }
        if (schoolYear.getEndDate() != null && semester.getEndDate() != null
                && semester.getEndDate().isAfter(schoolYear.getEndDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Học kỳ không thể kết thúc sau năm học");
        }
    }
}

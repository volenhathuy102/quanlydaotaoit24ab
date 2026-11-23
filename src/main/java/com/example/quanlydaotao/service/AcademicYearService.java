package com.example.quanlydaotao.service;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.example.quanlydaotao.entity.AcademicYear;
import com.example.quanlydaotao.repository.AcademicYearRepository;
import java.util.UUID;

@Service
public class AcademicYearService extends AbstractCrudService<AcademicYear> {

    public AcademicYearService(AcademicYearRepository repository) {
        super(repository);
    }

    @Override
    public AcademicYear create(AcademicYear entity) {
        validateDates(entity);
        return super.create(entity);
    }

    @Override
    public AcademicYear update(UUID id, AcademicYear entity) {
        validateDates(entity);
        return super.update(id, entity);
    }

    private void validateDates(AcademicYear year) {
        if (year.getStartDate() != null && year.getEndDate() != null
                && year.getStartDate().isAfter(year.getEndDate())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Ngày bắt đầu niên khóa phải trước hoặc bằng ngày kết thúc");
        }
    }
}

package com.example.quanlydaotao.service;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.quanlydaotao.entity.SchoolYear;
import com.example.quanlydaotao.repository.SchoolYearRepository;
import java.util.UUID;

@Service
public class SchoolYearService extends AbstractCrudService<SchoolYear> {

    public SchoolYearService(SchoolYearRepository repository) {
        super(repository);
    }

    @Override
    public SchoolYear create(SchoolYear entity) {
        validateDates(entity);
        return super.create(entity);
    }

    @Override
    public SchoolYear update(UUID id, SchoolYear entity) {
        validateDates(entity);
        return super.update(id, entity);
    }

    private void validateDates(SchoolYear year) {
        if (year.getStartDate() != null && year.getEndDate() != null
                && year.getStartDate().isAfter(year.getEndDate())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Ngày bắt đầu năm học phải trước hoặc bằng ngày kết thúc");
        }
    }
}

package com.example.quanlydaotao.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.quanlydaotao.entity.AcademicYear;
import com.example.quanlydaotao.service.AcademicYearService;

@RestController
@RequestMapping("/api/academic-years")
public class AcademicYearController extends AbstractCrudController<AcademicYear> {

    public AcademicYearController(AcademicYearService service) {
        super(service);
    }
}

package com.example.quanlydaotao.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.quanlydaotao.entity.SchoolYear;
import com.example.quanlydaotao.service.SchoolYearService;

@RestController
@RequestMapping("/api/school-years")
public class SchoolYearController extends AbstractCrudController<SchoolYear> {

    public SchoolYearController(SchoolYearService service) {
        super(service);
    }
}

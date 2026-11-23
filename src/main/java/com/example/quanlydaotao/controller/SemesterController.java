package com.example.quanlydaotao.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.quanlydaotao.entity.Semester;
import com.example.quanlydaotao.service.SemesterService;

@RestController
@RequestMapping("/api/semesters")
public class SemesterController extends AbstractCrudController<Semester> {

    public SemesterController(SemesterService service) {
        super(service);
    }
}

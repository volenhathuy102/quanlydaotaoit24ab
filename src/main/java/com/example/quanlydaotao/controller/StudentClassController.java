package com.example.quanlydaotao.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.quanlydaotao.entity.StudentClass;
import com.example.quanlydaotao.service.StudentClassService;

@RestController
@RequestMapping("/api/student-classes")
public class StudentClassController extends AbstractCrudController<StudentClass> {
    public StudentClassController(StudentClassService service) {
        super(service);
    }
}

package com.example.quanlydaotao.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.quanlydaotao.entity.LecturerCourseClass;
import com.example.quanlydaotao.service.LecturerCourseClassService;

@RestController
@RequestMapping("/api/lecturer-course-classes")
public class LecturerCourseClassController extends AbstractCrudController<LecturerCourseClass> {

    public LecturerCourseClassController(LecturerCourseClassService service) {
        super(service);
    }
}

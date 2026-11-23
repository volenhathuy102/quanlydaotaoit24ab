package com.example.quanlydaotao.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.quanlydaotao.entity.CourseSection;
import com.example.quanlydaotao.service.CourseSectionService;

@RestController
@RequestMapping("/api/course-sections")
public class CourseSectionController extends AbstractCrudController<CourseSection> {

    public CourseSectionController(CourseSectionService service) {
        super(service);
    }
}

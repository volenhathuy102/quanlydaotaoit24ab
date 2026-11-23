package com.example.quanlydaotao.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.quanlydaotao.entity.Course;
import com.example.quanlydaotao.service.CourseService;

@RestController
@RequestMapping("/api/courses")
public class CourseController extends AbstractCrudController<Course> {
    public CourseController(CourseService service) {
        super(service);
    }
}

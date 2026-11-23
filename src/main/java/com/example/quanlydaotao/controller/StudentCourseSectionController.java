package com.example.quanlydaotao.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.quanlydaotao.entity.StudentCourseSection;
import com.example.quanlydaotao.service.StudentCourseSectionService;

@RestController
@RequestMapping("/api/student-course-sections")
public class StudentCourseSectionController extends AbstractCrudController<StudentCourseSection> {

    public StudentCourseSectionController(StudentCourseSectionService service) {
        super(service);
    }
}

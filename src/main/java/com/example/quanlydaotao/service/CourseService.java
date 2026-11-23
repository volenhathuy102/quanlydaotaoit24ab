package com.example.quanlydaotao.service;

import org.springframework.stereotype.Service;
import com.example.quanlydaotao.entity.Course;
import com.example.quanlydaotao.repository.CourseRepository;

@Service
public class CourseService extends AbstractCrudService<Course> {
    public CourseService(CourseRepository repository) {
        super(repository);
    }
}

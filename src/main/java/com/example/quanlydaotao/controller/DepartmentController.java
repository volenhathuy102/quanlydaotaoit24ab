package com.example.quanlydaotao.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.quanlydaotao.entity.Department;
import com.example.quanlydaotao.service.DepartmentService;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController extends AbstractCrudController<Department> {
    public DepartmentController(DepartmentService service) {
        super(service);
    }
}

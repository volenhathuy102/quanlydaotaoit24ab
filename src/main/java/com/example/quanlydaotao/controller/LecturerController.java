package com.example.quanlydaotao.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.quanlydaotao.entity.Lecturer;
import com.example.quanlydaotao.service.LecturerService;

@RestController
@RequestMapping("/api/lecturers")
public class LecturerController extends AbstractCrudController<Lecturer> {
    public LecturerController(LecturerService service) {
        super(service);
    }
}

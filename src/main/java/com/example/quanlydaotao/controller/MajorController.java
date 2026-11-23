package com.example.quanlydaotao.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.quanlydaotao.entity.Major;
import com.example.quanlydaotao.service.MajorService;

@RestController
@RequestMapping("/api/majors")
public class MajorController extends AbstractCrudController<Major> {
    public MajorController(MajorService service) {
        super(service);
    }
}

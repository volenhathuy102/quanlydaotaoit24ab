package com.example.quanlydaotao.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.quanlydaotao.entity.TrainingProgram;
import com.example.quanlydaotao.service.TrainingProgramService;

@RestController
@RequestMapping("/api/training-programs")
public class TrainingProgramController extends AbstractCrudController<TrainingProgram> {
    public TrainingProgramController(TrainingProgramService service) {
        super(service);
    }
}

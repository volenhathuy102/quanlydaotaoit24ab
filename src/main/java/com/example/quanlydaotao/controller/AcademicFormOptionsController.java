package com.example.quanlydaotao.controller;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/academic-form-options")
public class AcademicFormOptionsController {

    private final JdbcTemplate jdbcTemplate;

    public AcademicFormOptionsController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/courses")
    public List<Map<String, Object>> getCourses() {
        return jdbcTemplate.queryForList("""
            SELECT id, code, name
            FROM courses
            WHERE deleted_at IS NULL
              AND (is_active = 1 OR is_active IS NULL)
            ORDER BY code
            """);
    }

    @GetMapping("/rooms")
    public List<Map<String, Object>> getRooms() {
        return jdbcTemplate.queryForList("""
            SELECT id, room_code AS code, room_name AS name, building_id AS buildingId
            FROM rooms
            WHERE deleted_at IS NULL
              AND (is_active = 1 OR is_active IS NULL)
            ORDER BY room_code
            """);
    }

    @GetMapping("/buildings")
    public List<Map<String, Object>> getBuildings() {
        return jdbcTemplate.queryForList("""
            SELECT id, code, name
            FROM buildings
            WHERE deleted_at IS NULL
              AND (is_active = 1 OR is_active IS NULL)
            ORDER BY code
            """);
    }
}

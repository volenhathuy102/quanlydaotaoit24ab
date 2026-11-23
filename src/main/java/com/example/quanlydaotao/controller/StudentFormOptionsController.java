
package com.example.quanlydaotao.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student-form-options")
public class StudentFormOptionsController {

    private final JdbcTemplate jdbcTemplate;

    public StudentFormOptionsController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/classes")
    public List<Map<String, Object>> getClasses() {
        return jdbcTemplate.queryForList("""
            SELECT c.id, c.code, c.name,
                   c.department_id AS departmentId,
                   c.major_id AS majorId,
                   c.training_program_id AS trainingProgramId,
                   c.academic_year_id AS academicYearId,
                   y.code AS academicYearCode,
                   y.name AS academicYearName,
                   y."year" AS academicYear
            FROM student_classes c
            LEFT JOIN academic_years y ON y.id = c.academic_year_id
            WHERE c.deleted_at IS NULL
              AND (c.is_active = 1 OR c.is_active IS NULL)
            ORDER BY c.code
            """);
    }

    @GetMapping("/departments")
    public List<Map<String, Object>> getDepartments() {
        return jdbcTemplate.queryForList("""
            SELECT id, code, name
            FROM departments
            WHERE deleted_at IS NULL
              AND (is_active = 1 OR is_active IS NULL)
            ORDER BY name
            """);
    }

    @GetMapping("/majors")
    public List<Map<String, Object>> getMajors(@RequestParam(required = false) UUID departmentId) {
        String sql = """
            SELECT id, code, name
            FROM majors
            WHERE deleted_at IS NULL
              AND (is_active = 1 OR is_active IS NULL)
            """ + (departmentId == null ? "" : " AND department_id = ?")
                + " ORDER BY name";
        return departmentId == null
                ? jdbcTemplate.queryForList(sql)
                : jdbcTemplate.queryForList(sql, departmentId);
    }

    @GetMapping("/training-programs")
    public List<Map<String, Object>> getTrainingPrograms(@RequestParam(required = false) UUID majorId) {
        String sql = """
            SELECT id, code, name
            FROM training_programs
            WHERE deleted_at IS NULL
              AND (is_active = 1 OR is_active IS NULL)
            """ + (majorId == null ? "" : " AND major_id = ?")
                + " ORDER BY name";
        return majorId == null
                ? jdbcTemplate.queryForList(sql)
                : jdbcTemplate.queryForList(sql, majorId);
    }

    @GetMapping("/statuses")
    public List<Map<String, Object>> getStatuses() {
        return jdbcTemplate.queryForList("""
            WITH ranked_statuses AS (
                SELECT id, code, name,
                       ROW_NUMBER() OVER (
                           PARTITION BY code, name
                           ORDER BY id
                       ) AS row_number
                FROM student_status
                WHERE deleted_at IS NULL
                  AND (is_active = 1 OR is_active IS NULL)
            )
            SELECT id, code, name
            FROM ranked_statuses
            WHERE row_number = 1
            ORDER BY name
            """);
    }
}

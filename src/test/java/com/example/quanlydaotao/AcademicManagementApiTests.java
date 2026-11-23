package com.example.quanlydaotao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.quanlydaotao.entity.Course;
import com.example.quanlydaotao.entity.CourseSection;
import com.example.quanlydaotao.entity.StudentStatus;
import com.example.quanlydaotao.repository.CourseRepository;
import com.example.quanlydaotao.repository.CourseSectionRepository;
import com.example.quanlydaotao.repository.StudentStatusRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AcademicManagementApiTests {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseSectionRepository courseSectionRepository;

    @Autowired
    private StudentStatusRepository studentStatusRepository;

    @Value("${local.server.port}")
    private int port;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Test
    void staticPagesAndApiDocumentationAreReachable() throws Exception {
        for (String path : List.of(
                "/",
                "/academic.html",
                "/assets/js/student.js",
                "/assets/js/academic.js",
                "/api/student-form-options/classes",
                "/api/student-form-options/departments",
                "/api/student-form-options/majors",
                "/api/student-form-options/training-programs",
                "/api/student-form-options/statuses",
                "/api/academic-form-options/courses",
                "/api/academic-form-options/rooms",
                "/api/academic-form-options/buildings",
                "/v3/api-docs")) {
            HttpResponse<String> response = send("http://localhost:" + port + path, "GET", null);
            assertEquals(200, response.statusCode(), () -> "GET " + path + " failed: " + response.body());
        }
    }

    @Test
    void allAcademicResourcesSupportCrudAndSoftDelete() throws Exception {
        String suffix = UUID.randomUUID().toString();
        List<String> cleanupUrls = new ArrayList<>();
        String academicYearId = null;
        String departmentId = null;
        String majorId = null;
        String trainingProgramId = null;
        String studentClassId = null;
        String lecturerId = null;
        String courseId = null;
        String schoolYearId = null;
        String semesterId = null;
        String courseSectionId = null;
        String studentId = null;
        String templateStatusId = null;
        String assignedStatusId = null;

        try {
            academicYearId = create("/api/academic-years", """
                    {"code":"AY-SEED-%s","name":"Seed academic year","year":"2025-2029"}
                    """.formatted(suffix), cleanupUrls);
            departmentId = create("/api/departments", """
                    {"code":"DEP-SEED-%s","name":"Seed department"}
                    """.formatted(suffix), cleanupUrls);
            majorId = create("/api/majors", """
                    {"code":"MAJ-SEED-%s","name":"Seed major","departmentId":"%s"}
                    """.formatted(suffix, departmentId), cleanupUrls);
            trainingProgramId = create("/api/training-programs", """
                    {"code":"TP-SEED-%s","name":"Seed training program","departmentId":"%s","majorId":"%s"}
                    """.formatted(suffix, departmentId, majorId), cleanupUrls);
            studentClassId = create("/api/student-classes", """
                    {"code":"CLASS-SEED-%s","name":"Seed student class","academicYearId":"%s","departmentId":"%s","majorId":"%s","trainingProgramId":"%s"}
                    """.formatted(suffix, academicYearId, departmentId, majorId, trainingProgramId), cleanupUrls);
            lecturerId = create("/api/lecturers", """
                    {"lecturerCode":"GV-SEED-%s","fullName":"Seed Lecturer","departmentId":"%s"}
                    """.formatted(suffix, departmentId), cleanupUrls);

            Course course = new Course();
            course.setCode("COURSE-SEED-" + suffix);
            course.setName("Seed course");
            courseId = courseRepository.save(course).getId().toString();

            schoolYearId = create("/api/school-years", """
                    {"code":"SY-%s","name":"2025-2026","startDate":"2025-08-01","endDate":"2026-07-31"}
                    """.formatted(suffix), cleanupUrls);
            semesterId = create("/api/semesters", """
                    {"code":"SEED-%s","name":"Seed semester","schoolYearId":"%s","startDate":"2025-08-01","endDate":"2025-12-31"}
                    """.formatted(suffix, schoolYearId), cleanupUrls);
            courseSectionId = create("/api/course-sections", """
                    {"code":"SEED-%s","name":"Seed section","courseId":"%s","semesterId":"%s","status":"open"}
                    """.formatted(suffix, courseId, semesterId), cleanupUrls);
            String studentCode = "SV" + (10_000_000 + Math.floorMod(suffix.hashCode(), 90_000_000));
            studentId = create("/api/students", """
                    {"code":"%s","fullName":"Test Student","studentClassessId":"%s"}
                    """.formatted(studentCode, studentClassId), cleanupUrls, 200);

            StudentStatus statusTemplate = new StudentStatus();
            statusTemplate.setStudentId(UUID.fromString(studentId));
            statusTemplate.setCode("STATUS-" + suffix);
            statusTemplate.setName("Active");
            templateStatusId = studentStatusRepository.save(statusTemplate).getId().toString();

            HttpResponse<String> statusOptions = send(
                    "http://localhost:" + port + "/api/student-form-options/statuses", "GET", null);
            assertEquals(200, statusOptions.statusCode());
            assertTrue(containsId(statusOptions.body(), templateStatusId), statusOptions.body());

            HttpResponse<String> studentWithStatus = send("http://localhost:" + port + "/api/students", "POST", """
                    {"code":"SV%d","fullName":"Student With Status","studentClassessId":"%s","statusId":"%s"}
                    """.formatted(900_000_000 + Math.floorMod(suffix.hashCode(), 99_999_999),
                            studentClassId, templateStatusId));
            assertEquals(200, studentWithStatus.statusCode(), studentWithStatus.body());
            JsonNode studentWithStatusJson = objectMapper.readTree(studentWithStatus.body());
            String statusStudentId = studentWithStatusJson.path("id").asText();
            assignedStatusId = studentWithStatusJson.path("statusId").asText();
            assertNotEquals(templateStatusId, assignedStatusId);
            StudentStatus assignedStatus = studentStatusRepository.findById(UUID.fromString(assignedStatusId))
                    .orElseThrow();
            assertEquals(statusStudentId, assignedStatus.getStudentId().toString());
            cleanupUrls.add("http://localhost:" + port + "/api/students/" + statusStudentId);

            List<String> endpoints = List.of(
                    "/api/departments",
                    "/api/majors",
                    "/api/training-programs",
                    "/api/academic-years",
                    "/api/student-classes",
                    "/api/lecturers",
                    "/api/school-years",
                    "/api/semesters",
                    "/api/course-sections",
                    "/api/students",
                    "/api/student-course-sections",
                    "/api/lecturer-course-classes");

            for (String endpoint : endpoints) {
                String url = "http://localhost:" + port + endpoint;
                String payload = payloadFor(endpoint, suffix, academicYearId, departmentId, majorId,
                        trainingProgramId, studentClassId, lecturerId, courseId, schoolYearId, semesterId,
                        courseSectionId, studentId);
                int expectedCreateStatus = "/api/students".equals(endpoint) ? 200 : 201;
                String id = create(endpoint, payload, cleanupUrls, expectedCreateStatus);
                assertNotNull(id);

                if ("/api/student-course-sections".equals(endpoint)) {
                    HttpResponse<String> duplicateResponse = send(url, "POST", payload);
                    assertEquals(409, duplicateResponse.statusCode());

                    CourseSection section = courseSectionRepository.findById(UUID.fromString(courseSectionId))
                            .orElseThrow();
                    String originalStatus = section.getStatus();
                    section.setStatus("closed");
                    courseSectionRepository.save(section);
                    try {
                        HttpResponse<String> cancelResponse = send(url + "/" + id, "PUT",
                                payload.replace("\"REGISTERED\"", "\"CANCELLED\""));
                        assertEquals(200, cancelResponse.statusCode());
                    } finally {
                        section.setStatus(originalStatus);
                        courseSectionRepository.save(section);
                    }
                }

                HttpResponse<String> listResponse = send(url, "GET", null);
                assertEquals(200, listResponse.statusCode());
                assertTrue(containsId(listResponse.body(), id));

                HttpResponse<String> updateResponse = send(url + "/" + id, "PUT",
                        payloadFor(endpoint, suffix, academicYearId, departmentId, majorId, trainingProgramId,
                                studentClassId, lecturerId, courseId, schoolYearId, semesterId, courseSectionId,
                                studentId, false));
                assertEquals(200, updateResponse.statusCode());
                assertFalse(objectMapper.readTree(updateResponse.body()).get("isActive").asBoolean());

                HttpResponse<String> deleteResponse = send(url + "/" + id, "DELETE", null);
                assertEquals("/api/students".equals(endpoint) ? 200 : 204, deleteResponse.statusCode());
                cleanupUrls.remove(url + "/" + id);

                HttpResponse<String> getResponse = send(url + "/" + id, "GET", null);
                assertEquals(404, getResponse.statusCode());

                HttpResponse<String> finalListResponse = send(url, "GET", null);
                assertEquals(200, finalListResponse.statusCode());
                assertFalse(containsId(finalListResponse.body(), id));
            }
        } finally {
            for (int i = cleanupUrls.size() - 1; i >= 0; i--) {
                send(cleanupUrls.get(i), "DELETE", null);
            }
            if (courseId != null) {
                courseRepository.deleteById(UUID.fromString(courseId));
            }
            if (assignedStatusId != null) {
                studentStatusRepository.deleteById(UUID.fromString(assignedStatusId));
            }
            if (templateStatusId != null) {
                studentStatusRepository.deleteById(UUID.fromString(templateStatusId));
            }
        }
    }

    private String create(String endpoint, String payload, List<String> cleanupUrls) throws Exception {
        return create(endpoint, payload, cleanupUrls, 201);
    }

    private String create(String endpoint, String payload, List<String> cleanupUrls, int expectedStatus)
            throws Exception {
        String url = "http://localhost:" + port + endpoint;
        HttpResponse<String> response = send(url, "POST", payload);
        assertEquals(expectedStatus, response.statusCode(),
                () -> "POST " + endpoint + " failed: " + response.body());
        String id = objectMapper.readTree(response.body()).get("id").asText();
        cleanupUrls.add(url + "/" + id);
        return id;
    }

    private String payloadFor(
            String endpoint,
            String suffix,
            String academicYearId,
            String departmentId,
            String majorId,
            String trainingProgramId,
            String studentClassId,
            String lecturerId,
            String courseId,
            String schoolYearId,
            String semesterId,
            String courseSectionId,
            String studentId) {
        return payloadFor(endpoint, suffix, academicYearId, departmentId, majorId, trainingProgramId,
                studentClassId, lecturerId, courseId, schoolYearId, semesterId, courseSectionId, studentId, true);
    }

    private String payloadFor(
            String endpoint,
            String suffix,
            String academicYearId,
            String departmentId,
            String majorId,
            String trainingProgramId,
            String studentClassId,
            String lecturerId,
            String courseId,
            String schoolYearId,
            String semesterId,
            String courseSectionId,
            String studentId,
            boolean isActive) {
        return switch (endpoint) {
            case "/api/departments" -> """
                    {"code":"DEP-%s","name":"Test department","isActive":%s}
                    """.formatted(suffix, isActive);
            case "/api/majors" -> """
                    {"code":"MAJ-%s","name":"Test major","departmentId":"%s","isActive":%s}
                    """.formatted(suffix, departmentId, isActive);
            case "/api/training-programs" -> """
                    {"code":"TP-%s","name":"Test training program","departmentId":"%s","majorId":"%s","isActive":%s}
                    """.formatted(suffix, departmentId, majorId, isActive);
            case "/api/academic-years" -> """
                    {"code":"AY-%s","name":"Test academic year","year":"2025-2029","isActive":%s}
                    """.formatted(suffix, isActive);
            case "/api/student-classes" -> """
                    {"code":"CLASS-%s","name":"Test student class","academicYearId":"%s","departmentId":"%s","majorId":"%s","trainingProgramId":"%s","isActive":%s}
                    """.formatted(suffix, academicYearId, departmentId, majorId, trainingProgramId, isActive);
            case "/api/lecturers" -> """
                    {"lecturerCode":"GV-%s","fullName":"Test Lecturer","departmentId":"%s","isActive":%s}
                    """.formatted(suffix, departmentId, isActive);
            case "/api/school-years" -> """
                    {"code":"SY-TEST-%s","name":"2025-2026","startDate":"2025-08-01","endDate":"2026-07-31","isActive":%s}
                    """.formatted(suffix, isActive);
            case "/api/semesters" -> """
                    {"code":"SEM-%s","name":"Test semester","schoolYearId":"%s","startDate":"2025-08-01","endDate":"2025-12-31","isActive":%s}
                    """.formatted(suffix, schoolYearId, isActive);
            case "/api/course-sections" -> """
                    {"code":"CS-%s","name":"Test course section","courseId":"%s","semesterId":"%s","maxStudents":20,"minStudents":1,"status":"open","isActive":%s}
                    """.formatted(suffix, courseId, semesterId, isActive);
            case "/api/students" -> """
                    {"code":"SV%s","fullName":"Test Student","studentClassessId":"%s","isActive":%s}
                    """.formatted((10_000_000 + Math.floorMod(suffix.hashCode(), 90_000_000)) + 1,
                            studentClassId, isActive);
            case "/api/student-course-sections" -> """
                    {"studentId":"%s","courseSectionId":"%s","registrationType":"first","status":"REGISTERED","isActive":%s}
                    """.formatted(studentId, courseSectionId, isActive);
            case "/api/lecturer-course-classes" -> """
                    {"lecturerId":"%s","courseClassId":"%s","role":"primary","isActive":%s}
                    """.formatted(lecturerId, courseSectionId, isActive);
            default -> throw new IllegalArgumentException("Unsupported endpoint: " + endpoint);
        };
    }

    private boolean containsId(String responseBody, String id) throws Exception {
        for (JsonNode record : objectMapper.readTree(responseBody)) {
            JsonNode idNode = record.has("id") ? record.get("id") : record.get("ID");
            if (idNode != null && id.equalsIgnoreCase(idNode.asText())) {
                return true;
            }
        }
        return false;
    }

    private HttpResponse<String> send(String url, String method, String body) throws Exception {
        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create(url));
        if (body != null) {
            request.header("Content-Type", "application/json")
                    .method(method, HttpRequest.BodyPublishers.ofString(body));
        } else {
            request.method(method, HttpRequest.BodyPublishers.noBody());
        }
        return httpClient.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }
}

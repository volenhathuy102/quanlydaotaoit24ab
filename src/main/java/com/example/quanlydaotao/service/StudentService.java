
package com.example.quanlydaotao.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import tools.jackson.databind.JsonNode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.quanlydaotao.entity.Student;
import com.example.quanlydaotao.entity.StudentClass;
import com.example.quanlydaotao.entity.StudentStatus;
import com.example.quanlydaotao.repository.StudentClassRepository;
import com.example.quanlydaotao.repository.StudentRepository;
import com.example.quanlydaotao.repository.StudentStatusRepository;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentClassRepository studentClassRepository;
    private final StudentStatusRepository studentStatusRepository;

    public StudentService(
            StudentRepository studentRepository,
            StudentClassRepository studentClassRepository,
            StudentStatusRepository studentStatusRepository) {
        this.studentRepository = studentRepository;
        this.studentClassRepository = studentClassRepository;
        this.studentStatusRepository = studentStatusRepository;
    }

    public List<Student> getAll(boolean includeInactive) {
        return studentRepository.findAll().stream()
                .filter(s -> s.getDeletedAt() == null)
                .filter(s -> includeInactive || !Boolean.FALSE.equals(s.getIsActive()))
                .collect(Collectors.toList());
    }

    public List<Student> search(String keyword, boolean includeInactive) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAll(includeInactive);
        }

        return studentRepository.searchStudents(keyword.trim()).stream()
                .filter(s -> s.getDeletedAt() == null)
                .filter(s -> includeInactive || !Boolean.FALSE.equals(s.getIsActive()))
                .collect(Collectors.toList());
    }

    public Student getById(UUID id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy sinh viên: " + id));

        if (student.getDeletedAt() != null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sinh viên đã bị xóa mềm");
        }

        return student;
    }

    @Transactional
    public Student save(Student student) {
        LocalDateTime now = LocalDateTime.now();
        boolean creating = student.getId() == null;
        UUID previousStatusId = null;
        StudentStatus selectedStatus = findStatus(student.getStatusId());
        if (student.getId() == null) {
            validateCodeFormat(student.getCode());
            applyClassDetails(student, null);
            validate(student, null);
            if (student.getCreatedAt() == null) {
                student.setCreatedAt(now);
            }
            if (student.getIsActive() == null) {
                student.setIsActive(true);
            }
        } else {
            Student old = studentRepository.findById(student.getId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Không tìm thấy sinh viên cần cập nhật"));
            previousStatusId = old.getStatusId();
            if (!Objects.equals(normalize(old.getCode()), normalize(student.getCode()))) {
                validateCodeFormat(student.getCode());
            }

            student.setCreatedAt(old.getCreatedAt());
            student.setCreatedBy(old.getCreatedBy());
            student.setDeletedAt(old.getDeletedAt());
            student.setDeletedBy(old.getDeletedBy());

            if (student.getIsActive() == null) {
                student.setIsActive(old.getIsActive());
            }
            applyClassDetails(student, old);
            validate(student, old.getId());
        }

        student.setCode(student.getCode().trim());
        student.setFullName(student.getFullName().trim());
        if (student.getEmail() != null) {
            student.setEmail(student.getEmail().trim());
        }
        if (student.getPhone() != null) {
            student.setPhone(student.getPhone().trim());
        }
        student.setUpdatedAt(now);
        Student savedStudent = studentRepository.save(student);
        if (selectedStatus == null) {
            return savedStudent;
        }

        if (!creating && previousStatusId != null) {
            StudentStatus previousStatus = studentStatusRepository.findByIdAndDeletedAtIsNull(previousStatusId)
                    .orElse(null);
            if (previousStatus != null) {
                if (!sameStatus(previousStatus, selectedStatus)) {
                    previousStatus.setCode(selectedStatus.getCode());
                    previousStatus.setName(selectedStatus.getName());
                    previousStatus.setDescription(selectedStatus.getDescription());
                    studentStatusRepository.save(previousStatus);
                }
                savedStudent.setStatusId(previousStatus.getId());
                return studentRepository.save(savedStudent);
            }
        }

        StudentStatus studentStatus = new StudentStatus();
        studentStatus.setStudentId(savedStudent.getId());
        studentStatus.setCode(selectedStatus.getCode());
        studentStatus.setName(selectedStatus.getName());
        studentStatus.setDescription(selectedStatus.getDescription());
        StudentStatus savedStatus = studentStatusRepository.save(studentStatus);
        savedStudent.setStatusId(savedStatus.getId());
        return studentRepository.save(savedStudent);
    }

    private StudentStatus findStatus(UUID statusId) {
        if (statusId == null) {
            return null;
        }
        return studentStatusRepository.findByIdAndDeletedAtIsNull(statusId)
                .filter(status -> !Boolean.FALSE.equals(status.getIsActive()))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Trạng thái sinh viên không tồn tại hoặc đã ngừng hoạt động"));
    }

    private boolean sameStatus(StudentStatus left, StudentStatus right) {
        return Objects.equals(normalize(left.getCode()), normalize(right.getCode()))
                && Objects.equals(normalize(left.getName()), normalize(right.getName()));
    }

    @Transactional
    public Student update(UUID id, JsonNode changes) {
        Student student = getById(id);
        if (changes.has("code") || changes.has("studentCode")) {
            student.setCode(textValue(changes.has("code") ? changes.get("code") : changes.get("studentCode")));
        }
        if (changes.has("userId")) student.setUserId(uuidValue(changes.get("userId")));
        if (changes.has("fullName") || changes.has("name")) {
            student.setFullName(textValue(changes.has("fullName") ? changes.get("fullName") : changes.get("name")));
        }
        if (changes.has("email")) student.setEmail(textValue(changes.get("email")));
        if (changes.has("phone")) student.setPhone(textValue(changes.get("phone")));
        if (changes.has("dateOfBirth")) student.setDateOfBirth(dateValue(changes.get("dateOfBirth")));
        if (changes.has("gender")) student.setGender(textValue(changes.get("gender")));
        if (changes.has("personalIdentificationNumber")) {
            student.setPersonalIdentificationNumber(textValue(changes.get("personalIdentificationNumber")));
        }
        if (changes.has("dateOfIssue")) student.setDateOfIssue(dateValue(changes.get("dateOfIssue")));
        if (changes.has("cardPlace")) student.setCardPlace(textValue(changes.get("cardPlace")));
        if (changes.has("address")) student.setAddress(textValue(changes.get("address")));
        if (changes.has("currentAddress")) student.setCurrentAddress(textValue(changes.get("currentAddress")));
        if (changes.has("academicYearYear")) {
            student.setAcademicYearYear(dateTimeValue(changes.get("academicYearYear")));
        }
        if (changes.has("departmentId")) student.setDepartmentId(uuidValue(changes.get("departmentId")));
        if (changes.has("majorId")) student.setMajorId(uuidValue(changes.get("majorId")));
        if (changes.has("trainingProgramId")) {
            student.setTrainingProgramId(uuidValue(changes.get("trainingProgramId")));
        }
        if (changes.has("statusId")) student.setStatusId(uuidValue(changes.get("statusId")));
        if (changes.has("studentClassessId")) {
            student.setStudentClassessId(uuidValue(changes.get("studentClassessId")));
        }
        if (changes.has("admissionYear")) student.setAdmissionYear(dateTimeValue(changes.get("admissionYear")));
        if (changes.has("isActive") && !changes.get("isActive").isNull()) {
            student.setIsActive(changes.get("isActive").asBoolean());
        }
        return save(student);
    }

    private String textValue(JsonNode node) {
        return node == null || node.isNull() || node.asText().isBlank() ? null : node.asText().trim();
    }

    private UUID uuidValue(JsonNode node) {
        String value = textValue(node);
        if (value == null) return null;
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Một giá trị liên kết không phải UUID hợp lệ");
        }
    }

    private LocalDate dateValue(JsonNode node) {
        String value = textValue(node);
        if (value == null) return null;
        try {
            return LocalDate.parse(value);
        } catch (java.time.format.DateTimeParseException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ngày tháng không hợp lệ");
        }
    }

    private LocalDateTime dateTimeValue(JsonNode node) {
        String value = textValue(node);
        if (value == null) return null;
        try {
            return LocalDateTime.parse(value);
        } catch (java.time.format.DateTimeParseException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thời gian không hợp lệ");
        }
    }

    private void validate(Student student, UUID currentId) {
        if (student.getCode() == null || student.getCode().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã sinh viên là bắt buộc");
        }
        if (student.getFullName() == null || student.getFullName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Họ và tên là bắt buộc");
        }
        if (student.getStudentClassessId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vui lòng chọn lớp sinh viên");
        }
        boolean duplicateCode = currentId == null
                ? studentRepository.existsByCodeIgnoreCase(student.getCode().trim())
                : studentRepository.existsByCodeIgnoreCaseAndIdNot(student.getCode().trim(), currentId);
        if (duplicateCode) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Mã sinh viên đã tồn tại");
        }
        if (student.getEmail() != null && !student.getEmail().isBlank()) {
            if (!student.getEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email không hợp lệ");
            }
            boolean duplicateEmail = currentId == null
                    ? studentRepository.existsByEmailIgnoreCase(student.getEmail().trim())
                    : studentRepository.existsByEmailIgnoreCaseAndIdNot(student.getEmail().trim(), currentId);
            if (duplicateEmail) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email đã được sử dụng");
            }
        }
        if (student.getPhone() != null && !student.getPhone().isBlank()
                && !student.getPhone().matches("^[+0-9() .-]{7,20}$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số điện thoại không hợp lệ");
        }
    }

    private void validateCodeFormat(String code) {
        if (code == null || !code.trim().matches("(?i)^SV[0-9]{4,}$")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Mã sinh viên mới phải theo mẫu SV0001 (SV và ít nhất 4 chữ số)");
        }
    }

    private void applyClassDetails(Student student, Student previous) {
        if (student.getStudentClassessId() == null) {
            if (previous == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vui lòng chọn lớp sinh viên");
            }
            return;
        }
        if (previous != null && Objects.equals(previous.getStudentClassessId(), student.getStudentClassessId())) {
            return;
        }
        StudentClass studentClass = studentClassRepository.findByIdAndDeletedAtIsNull(student.getStudentClassessId())
                .filter(item -> !Boolean.FALSE.equals(item.getIsActive()))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Lớp sinh viên không tồn tại hoặc đã ngừng hoạt động"));
        if (studentClass.getDepartmentId() != null) {
            student.setDepartmentId(studentClass.getDepartmentId());
        }
        if (studentClass.getMajorId() != null) {
            student.setMajorId(studentClass.getMajorId());
        }
        if (studentClass.getTrainingProgramId() != null) {
            student.setTrainingProgramId(studentClass.getTrainingProgramId());
        }
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    @Transactional
    public void delete(UUID id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy sinh viên: " + id));

        student.setIsActive(false);
        student.setDeletedAt(LocalDateTime.now());
        student.setUpdatedAt(LocalDateTime.now());

        studentRepository.save(student);
    }
}

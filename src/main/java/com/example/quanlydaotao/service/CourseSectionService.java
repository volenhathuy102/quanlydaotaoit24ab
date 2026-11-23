package com.example.quanlydaotao.service;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.quanlydaotao.entity.Room;
import com.example.quanlydaotao.entity.CourseSection;
import com.example.quanlydaotao.repository.BuildingRepository;
import com.example.quanlydaotao.repository.CourseRepository;
import com.example.quanlydaotao.repository.CourseSectionRepository;
import com.example.quanlydaotao.repository.RoomRepository;
import com.example.quanlydaotao.repository.SemesterRepository;

@Service
public class CourseSectionService extends AbstractCrudService<CourseSection> {

    private final SemesterRepository semesterRepository;
    private final CourseRepository courseRepository;
    private final RoomRepository roomRepository;
    private final BuildingRepository buildingRepository;

    public CourseSectionService(
            CourseSectionRepository repository,
            SemesterRepository semesterRepository,
            CourseRepository courseRepository,
            RoomRepository roomRepository,
            BuildingRepository buildingRepository) {
        super(repository);
        this.semesterRepository = semesterRepository;
        this.courseRepository = courseRepository;
        this.roomRepository = roomRepository;
        this.buildingRepository = buildingRepository;
    }

    @Override
    public CourseSection create(CourseSection section) {
        validate(section);
        return super.create(section);
    }

    @Override
    public CourseSection update(UUID id, CourseSection section) {
        validate(section);
        return super.update(id, section);
    }

    private void validate(CourseSection section) {
        if (section.getSemesterId() == null
                || semesterRepository.findByIdAndDeletedAtIsNull(section.getSemesterId()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vui lòng chọn học kỳ còn hiệu lực");
        }
        if (section.getCourseId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vui lòng chọn học phần");
        }
        courseRepository.findByIdAndDeletedAtIsNull(section.getCourseId())
                .filter(course -> !Boolean.FALSE.equals(course.getIsActive()))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Học phần không tồn tại hoặc đã ngừng hoạt động"));
        if (section.getRoomId() != null) {
            Room room = roomRepository.findByIdAndDeletedAtIsNull(section.getRoomId())
                    .filter(item -> !Boolean.FALSE.equals(item.getIsActive()))
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Phòng học không tồn tại hoặc đã ngừng hoạt động"));
            if (section.getBuildingId() != null && !section.getBuildingId().equals(room.getBuildingId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phòng học không thuộc tòa nhà được chọn");
            }
        }
        if (section.getBuildingId() != null) {
            buildingRepository.findByIdAndDeletedAtIsNull(section.getBuildingId())
                    .filter(item -> !Boolean.FALSE.equals(item.getIsActive()))
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Tòa nhà không tồn tại hoặc đã ngừng hoạt động"));
        }
        if (section.getMinStudents() != null && section.getMaxStudents() != null
                && section.getMinStudents() > section.getMaxStudents()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Sĩ số tối thiểu không được lớn hơn sĩ số tối đa");
        }
        if (section.getMaxStudents() != null && section.getMaxStudents() < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sĩ số tối đa phải lớn hơn 0");
        }
        if (section.getMinStudents() != null && section.getMinStudents() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sĩ số tối thiểu không được âm");
        }
        if (section.getRegistrationStart() != null && section.getRegistrationEnd() != null
                && section.getRegistrationStart().isAfter(section.getRegistrationEnd())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Thời gian bắt đầu đăng ký phải trước hoặc bằng thời gian kết thúc");
        }
    }
}

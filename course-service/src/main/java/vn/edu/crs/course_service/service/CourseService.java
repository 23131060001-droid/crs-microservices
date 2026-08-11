package vn.edu.crs.course_service.service;

import vn.edu.crs.course_service.dto.CourseDTO;
import vn.edu.crs.course_service.entity.Course;
import vn.edu.crs.course_service.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    // ========== Các phương thức CRUD từ Buổi 2 ==========

    public Page<CourseDTO> getAllCourses(Pageable pageable) {
        return courseRepository.findAll(pageable).map(this::toDTO);
    }

    public CourseDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy môn học với id = " + id));
        return toDTO(course);
    }

    @Transactional
    public CourseDTO createCourse(CourseDTO dto) {
        if (courseRepository.existsByTenMonHocIgnoreCase(dto.getTenMonHoc())) {
            throw new IllegalArgumentException("Tên môn học đã tồn tại");
        }
        Course course = toEntity(dto);
        Course saved = courseRepository.save(course);
        return toDTO(saved);
    }

    @Transactional
    public CourseDTO updateCourse(Long id, CourseDTO dto) {
        Course existing = courseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy môn học với id = " + id));

        if (!existing.getTenMonHoc().equalsIgnoreCase(dto.getTenMonHoc())
                && courseRepository.existsByTenMonHocIgnoreCase(dto.getTenMonHoc())) {
            throw new IllegalArgumentException("Tên môn học đã tồn tại");
        }

        existing.setTenMonHoc(dto.getTenMonHoc());
        existing.setSoChoToiDa(dto.getSoChoToiDa());
        // Không cập nhật soChoConLai trực tiếp qua update

        Course updated = courseRepository.save(existing);
        return toDTO(updated);
    }

    @Transactional
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new NoSuchElementException("Không tìm thấy môn học với id = " + id);
        }
        courseRepository.deleteById(id);
    }

    // ========== Phương thức tìm kiếm phân trang (Buổi 3) ==========

    public Page<CourseDTO> search(String keyword, Pageable pageable) {
        Page<Course> page;
        if (keyword == null || keyword.isBlank()) {
            page = courseRepository.findAll(pageable);
        } else {
            page = courseRepository.findByTenMonHocContainingIgnoreCase(keyword, pageable);
        }
        return page.map(this::toDTO);
    }

    // ========== Phương thức dành cho internal API (Buổi 3) ==========

    @Transactional
    public CourseDTO reserveSeat(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy môn học với id = " + courseId));
        if (course.getSoChoConLai() <= 0) {
            throw new IllegalStateException("Môn học đã hết chỗ, không thể đăng ký");
        }
        course.setSoChoConLai(course.getSoChoConLai() - 1);
        return toDTO(courseRepository.save(course));
    }

    @Transactional
    public CourseDTO releaseSeat(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy môn học với id = " + courseId));
        if (course.getSoChoConLai() < course.getSoChoToiDa()) {
            course.setSoChoConLai(course.getSoChoConLai() + 1);
        }
        return toDTO(courseRepository.save(course));
    }

    // ========== Phương thức chuyển đổi ==========

    private CourseDTO toDTO(Course course) {
        CourseDTO dto = new CourseDTO();
        dto.setId(course.getId());
        dto.setTenMonHoc(course.getTenMonHoc());
        dto.setSoChoToiDa(course.getSoChoToiDa());
        dto.setSoChoConLai(course.getSoChoConLai());
        return dto;
    }

    private Course toEntity(CourseDTO dto) {
        Course course = new Course();
        course.setTenMonHoc(dto.getTenMonHoc());
        course.setSoChoToiDa(dto.getSoChoToiDa());
        course.setSoChoConLai(dto.getSoChoToiDa());
        return course;
    }
}
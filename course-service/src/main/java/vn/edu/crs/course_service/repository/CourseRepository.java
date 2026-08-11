package vn.edu.crs.course_service.repository;

import vn.edu.crs.course_service.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
    // Buổi 2: kiểm tra tên môn học đã tồn tại (không phân biệt hoa/thường)
    boolean existsByTenMonHocIgnoreCase(String tenMonHoc);

    // Buổi 3: tìm kiếm môn học theo tên (chứa từ khóa, không phân biệt hoa/thường) và phân trang
    Page<Course> findByTenMonHocContainingIgnoreCase(String keyword, Pageable pageable);
}
package vn.edu.crs.course_service.controller;

import vn.edu.crs.course_service.dto.CourseDTO;
import vn.edu.crs.course_service.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    // GET /courses?keyword=...&page=...&size=...&sort=...
    @GetMapping
    public Page<CourseDTO> search(
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        return courseService.search(keyword, pageable);
    }

    @GetMapping("/{id}")
    public CourseDTO getById(@PathVariable Long id) {
        return courseService.getCourseById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseDTO create(@Valid @RequestBody CourseDTO dto) {
        return courseService.createCourse(dto);
    }

    @PutMapping("/{id}")
    public CourseDTO update(@PathVariable Long id, @Valid @RequestBody CourseDTO dto) {
        return courseService.updateCourse(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        courseService.deleteCourse(id);
    }
}
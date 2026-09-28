package com.techedge.portal.controller;

import com.techedge.portal.dto.request.CourseRequest;
import com.techedge.portal.dto.response.ApiResponse;
import com.techedge.portal.dto.response.BatchResponse;
import com.techedge.portal.dto.response.CourseDetailResponse;
import com.techedge.portal.dto.response.CourseResponse;
import com.techedge.portal.dto.response.PageResponse;
import com.techedge.portal.entity.enums.CourseCategory;
import com.techedge.portal.entity.enums.CourseLevel;
import com.techedge.portal.service.BatchService;
import com.techedge.portal.service.CourseOfferService;
import com.techedge.portal.service.CourseService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
@SecurityRequirement(name = "bearerAuth")

public class CourseController {

    private final CourseService courseService;
    private final BatchService batchService;
    private final CourseOfferService courseOfferService;

    public CourseController(
            CourseService courseService,
            BatchService batchService,
            CourseOfferService courseOfferService) {

        this.courseService = courseService;
        this.batchService = batchService;
        this.courseOfferService = courseOfferService;
    }

    // =========================================================
    // GET ALL COURSES
    // Public
    // =========================================================

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CourseResponse>>> getCourses(
            @RequestParam(required = false) CourseCategory category,
            @RequestParam(required = false) CourseLevel level,
            @RequestParam(required = false) BigDecimal minFee,
            @RequestParam(required = false) BigDecimal maxFee,
            @RequestParam(required = false) String search,
            @PageableDefault(
                    size = 10,
                    sort = "title",
                    direction = Sort.Direction.ASC
            ) Pageable pageable) {

        Page<CourseResponse> page =
                courseService.getCourses(
                        category,
                        level,
                        minFee,
                        maxFee,
                        search,
                        pageable
                );

        PageResponse<CourseResponse> pageResponse =
                new PageResponse<>(
                        page.getContent(),
                        page.getNumber(),
                        page.getSize(),
                        page.getTotalElements(),
                        page.getTotalPages(),
                        page.isLast()
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Courses fetched successfully",
                        pageResponse
                )
        );
    }

    // =========================================================
    // GET COURSE BY ID
    // Public
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseDetailResponse>> getCourseById(
            @PathVariable Long id) {

        CourseDetailResponse response =
                courseService.getCourseById(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Course fetched successfully",
                        response
                )
        );
    }

    // =========================================================
    // GET BATCHES OF COURSE
    // Public
    // =========================================================

    @GetMapping("/{id}/batches")
    public ResponseEntity<ApiResponse<List<BatchResponse>>> getCourseBatches(
            @PathVariable Long id) {

        List<BatchResponse> response =
                batchService.getBatchesByCourse(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Course batches fetched successfully",
                        response
                )
        );
    }

    // =========================================================
    // CREATE COURSE
    // ADMIN ONLY
    // =========================================================

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(
            @Valid @RequestBody CourseRequest request) {

        CourseResponse response =
                courseService.createCourse(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "Course created successfully",
                                response
                        )
                );
    }

    // =========================================================
    // UPDATE COURSE
    // ADMIN ONLY
    // =========================================================

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseRequest request) {

        CourseResponse response =
                courseService.updateCourse(id, request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Course updated successfully",
                        response
                )
        );
    }

    // =========================================================
    // DELETE COURSE
    // ADMIN ONLY
    // =========================================================

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable Long id) {

        courseService.deleteCourse(id);

        return ResponseEntity.noContent().build();
    }

    // =========================================================
    // LINK OFFER TO COURSE
    // ADMIN ONLY
    // =========================================================

    @PostMapping("/{courseId}/offers/{offerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> linkOffer(
            @PathVariable Long courseId,
            @PathVariable Long offerId) {

        courseOfferService.linkOffer(courseId, offerId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Offer linked to course successfully",
                        null
                )
        );
    }

    // =========================================================
    // UNLINK OFFER FROM COURSE
    // ADMIN ONLY
    // =========================================================

    @DeleteMapping("/{courseId}/offers/{offerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> unlinkOffer(
            @PathVariable Long courseId,
            @PathVariable Long offerId) {

        courseOfferService.unlinkOffer(courseId, offerId);

        return ResponseEntity.noContent().build();
    }
}
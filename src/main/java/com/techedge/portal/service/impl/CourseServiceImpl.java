package com.techedge.portal.service.impl;

import com.techedge.portal.dto.request.CourseRequest;
import com.techedge.portal.dto.response.BatchResponse;
import com.techedge.portal.dto.response.CourseDetailResponse;
import com.techedge.portal.dto.response.CourseResponse;
import com.techedge.portal.dto.response.OfferResponse;
import com.techedge.portal.dto.response.TopicResponse;
import com.techedge.portal.entity.Batch;
import com.techedge.portal.entity.enums.BatchStatus;
import com.techedge.portal.entity.Course;
import com.techedge.portal.entity.enums.CourseCategory;
import com.techedge.portal.entity.enums.CourseLevel;
import com.techedge.portal.entity.CourseOffer;
import com.techedge.portal.entity.Offer;
import com.techedge.portal.exception.BusinessRuleException;
import com.techedge.portal.exception.ResourceNotFoundException;
import com.techedge.portal.mapper.BatchMapper;
import com.techedge.portal.mapper.CourseMapper;
import com.techedge.portal.mapper.OfferMapper;
import com.techedge.portal.repository.BatchRepository;
import com.techedge.portal.repository.CourseOfferRepository;
import com.techedge.portal.repository.CourseRepository;
import com.techedge.portal.repository.EnrollmentRepository;
import com.techedge.portal.repository.TopicRepository;
import com.techedge.portal.service.CourseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final TopicRepository topicRepository;
    private final BatchRepository batchRepository;
    private final CourseOfferRepository courseOfferRepository;
    private final EnrollmentRepository enrollmentRepository;

    public CourseServiceImpl(
            CourseRepository courseRepository,
            TopicRepository topicRepository,
            BatchRepository batchRepository,
            CourseOfferRepository courseOfferRepository,
            EnrollmentRepository enrollmentRepository
    ) {
        this.courseRepository = courseRepository;
        this.topicRepository = topicRepository;
        this.batchRepository = batchRepository;
        this.courseOfferRepository = courseOfferRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponse> getCourses(
            CourseCategory category,
            CourseLevel level,
            BigDecimal minFee,
            BigDecimal maxFee,
            String search,
            Pageable pageable
    ) {

        Page<Course> courses = courseRepository.searchActiveCourses(
                category,
                level,
                minFee,
                maxFee,
                normalizeSearch(search),
                pageable
        );

        return courses.map(this::buildCourseResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseDetailResponse getCourseById(Long courseId) {

        Course course = courseRepository.findById(courseId)
                .filter(Course::getActive)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Course not found with id: " + courseId
                        )
                );

        List<TopicResponse> topics = topicRepository
                .findByCourseIdOrderBySortOrderAsc(courseId)
                .stream()
                .map(topic -> new TopicResponse(
                        topic.getId(),
                        topic.getTitle(),
                        topic.getSortOrder()
                ))
                .toList();

        List<BatchResponse> batches = batchRepository
                .findByCourseId(courseId)
                .stream()
                .map(BatchMapper::toResponse)
                .toList();

        List<CourseOffer> liveCourseOffers =
                courseOfferRepository.findLiveOffersByCourseId(
                        courseId,
                        LocalDate.now()
                );

        List<OfferResponse> offers = liveCourseOffers
                .stream()
                .map(CourseOffer::getOffer)
                .map(OfferMapper::toResponse)
                .toList();

        Offer selectedOffer = selectBestOffer(liveCourseOffers);

        BigDecimal discountedFee =
                calculateDiscountedFee(course.getFee(), selectedOffer);

        return new CourseDetailResponse(
                course.getId(),
                course.getCode(),
                course.getTitle(),
                course.getDescription(),
                course.getCategory(),
                course.getLevel(),
                course.getDurationWeeks(),
                course.getFee(),
                discountedFee,
                topics,
                batches,
                offers,
                null,
                null
        );
    }

    @Override
    public CourseResponse createCourse(CourseRequest request) {

        if (courseRepository.existsByCode(request.code())) {
            throw new BusinessRuleException(
                    "Course code already exists: " + request.code()
            );
        }

        Course course = CourseMapper.toEntity(request);

        course.setActive(true);

        Course savedCourse = courseRepository.save(course);

        return buildCourseResponse(savedCourse);
    }

    @Override
    public CourseResponse updateCourse(
            Long courseId,
            CourseRequest request
    ) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Course not found with id: " + courseId
                        )
                );

        courseRepository.findByCode(request.code())
                .filter(existing -> !existing.getId().equals(courseId))
                .ifPresent(existing -> {
                    throw new BusinessRuleException(
                            "Course code already exists: " + request.code()
                    );
                });

        CourseMapper.updateEntity(course, request);

        Course updatedCourse = courseRepository.save(course);

        return buildCourseResponse(updatedCourse);
    }

    @Override
    public void deleteCourse(Long courseId) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Course not found with id: " + courseId
                        )
                );

        if (enrollmentRepository.existsByCourseId(courseId)) {
            throw new BusinessRuleException(
                    "Course cannot be deleted because enrollments exist"
            );
        }

        course.setActive(false);
        courseRepository.save(course);
    }

    private CourseResponse buildCourseResponse(Course course) {

        List<CourseOffer> liveOffers =
                courseOfferRepository.findLiveOffersByCourseId(
                        course.getId(),
                        LocalDate.now()
                );

        Offer selectedOffer = selectBestOffer(liveOffers);

        BigDecimal discountedFee =
                calculateDiscountedFee(course.getFee(), selectedOffer);

        String activeOfferTitle =
                selectedOffer != null
                        ? selectedOffer.getTitle()
                        : null;

        Long upcomingBatchCount =
                batchRepository.countByCourseIdAndStatus(
                        course.getId(),
                        BatchStatus.UPCOMING
                );

        return CourseMapper.toResponse(
                course,
                discountedFee,
                activeOfferTitle,
                upcomingBatchCount
        );
    }

    private Offer selectBestOffer(List<CourseOffer> courseOffers) {

        return courseOffers.stream()
                .map(CourseOffer::getOffer)
                .max(
                        Comparator
                                .comparing(
                                        Offer::getDiscountPercent
                                )
                                .thenComparing(
                                        Offer::getValidTo,
                                        Comparator.reverseOrder()
                                )
                                .thenComparing(
                                        Offer::getId,
                                        Comparator.reverseOrder()
                                )
                )
                .orElse(null);
    }

    private BigDecimal calculateDiscountedFee(
            BigDecimal fee,
            Offer offer
    ) {

        if (offer == null) {
            return null;
        }

        BigDecimal discount = fee
                .multiply(offer.getDiscountPercent())
                .divide(
                        BigDecimal.valueOf(100),
                        2,
                        RoundingMode.HALF_UP
                );

        return fee.subtract(discount)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private String normalizeSearch(String search) {

        if (search == null || search.isBlank()) {
            return null;
        }

        return search.trim();
    }
}
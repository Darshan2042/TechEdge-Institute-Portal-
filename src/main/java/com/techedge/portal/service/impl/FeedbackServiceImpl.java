package com.techedge.portal.service.impl;

import com.techedge.portal.dto.request.FeedbackRequest;
import com.techedge.portal.dto.response.FeedbackResponse;
import com.techedge.portal.entity.Course;
import com.techedge.portal.entity.enums.EnrollmentStatus;
import com.techedge.portal.entity.Feedback;
import com.techedge.portal.entity.Placement;
import com.techedge.portal.entity.Student;
import com.techedge.portal.exception.BusinessRuleException;
import com.techedge.portal.exception.ForbiddenException;
import com.techedge.portal.exception.ResourceNotFoundException;
import com.techedge.portal.mapper.FeedbackMapper;
import com.techedge.portal.repository.CourseRepository;
import com.techedge.portal.repository.EnrollmentRepository;
import com.techedge.portal.repository.FeedbackRepository;
import com.techedge.portal.repository.PlacementRepository;
import com.techedge.portal.repository.StudentRepository;
import com.techedge.portal.service.FeedbackService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final PlacementRepository placementRepository;

    public FeedbackServiceImpl(
            FeedbackRepository feedbackRepository,
            StudentRepository studentRepository,
            CourseRepository courseRepository,
            EnrollmentRepository enrollmentRepository,
            PlacementRepository placementRepository
    ) {
        this.feedbackRepository = feedbackRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.placementRepository = placementRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackResponse> getApprovedFeedback() {

        return feedbackRepository.findByApprovedTrue()
                .stream()
                .map(this::mapFeedback)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackResponse> getPendingFeedback() {

        return feedbackRepository.findByApprovedFalse()
                .stream()
                .map(this::mapFeedback)
                .toList();
    }

    @Override
    public FeedbackResponse submitFeedback(
            Long userId,
            FeedbackRequest request
    ) {

        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found for user: " + userId
                        ));

        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Course not found: " + request.courseId()
                        ));

        boolean completed = enrollmentRepository
                .existsByStudentIdAndCourseIdAndStatus(
                        student.getId(),
                        course.getId(),
                        EnrollmentStatus.COMPLETED
                );

        if (!completed) {
            throw new BusinessRuleException(
                    "Feedback can only be submitted for a completed course"
            );
        }

        if (feedbackRepository.existsByStudentIdAndCourseId(
                student.getId(),
                course.getId()
        )) {
            throw new BusinessRuleException(
                    "Feedback already submitted for this course"
            );
        }

        Feedback feedback = new Feedback();

        feedback.setStudent(student);
        feedback.setCourse(course);
        feedback.setRating(request.rating());
        feedback.setComments(request.comments());

        // New feedback must always wait for admin approval.
        feedback.setApproved(false);

        return mapFeedback(
                feedbackRepository.save(feedback)
        );
    }

    @Override
    public FeedbackResponse approveFeedback(Long feedbackId) {

        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Feedback not found: " + feedbackId
                        ));

        feedback.setApproved(true);

        return mapFeedback(
                feedbackRepository.save(feedback)
        );
    }

    @Override
    public void deleteFeedback(
            Long feedbackId,
            Long userId,
            boolean admin
    ) {

        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Feedback not found: " + feedbackId
                        ));

        if (!admin) {

            Student student = studentRepository.findByUserId(userId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Student not found for user: " + userId
                            ));

            if (!feedback.getStudent().getId()
                    .equals(student.getId())) {

                throw new ForbiddenException(
                        "You are not allowed to delete this feedback"
                );
            }
        }

        feedbackRepository.delete(feedback);
    }

    private FeedbackResponse mapFeedback(Feedback feedback) {

        String placedAt = null;

        Placement placement =
                placementRepository
                        .findFirstByStudentIdOrderByPlacedOnDesc(
                                feedback.getStudent().getId()
                        )
                        .orElse(null);

        if (placement != null) {
            placedAt = placement.getCompany().getName();
        }

        return FeedbackMapper.toResponse(
                feedback,
                placedAt
        );
    }
}
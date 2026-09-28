package com.techedge.portal.service.impl;

import com.techedge.portal.dto.request.BatchRequest;
import com.techedge.portal.dto.response.BatchResponse;
import com.techedge.portal.entity.Batch;
import com.techedge.portal.entity.enums.BatchMode;
import com.techedge.portal.entity.enums.BatchStatus;
import com.techedge.portal.entity.Course;
import com.techedge.portal.exception.BusinessRuleException;
import com.techedge.portal.exception.ResourceNotFoundException;
import com.techedge.portal.mapper.BatchMapper;
import com.techedge.portal.repository.BatchRepository;
import com.techedge.portal.repository.CourseRepository;
import com.techedge.portal.service.BatchService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class BatchServiceImpl implements BatchService {

    private final BatchRepository batchRepository;
    private final CourseRepository courseRepository;

    public BatchServiceImpl(
            BatchRepository batchRepository,
            CourseRepository courseRepository
    ) {
        this.batchRepository = batchRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BatchResponse> getBatches(
            BatchStatus status,
            BatchMode mode
    ) {
        return batchRepository.findAllFiltered(status, mode)
                .stream()
                .map(BatchMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BatchResponse> getBatchesByCourse(Long courseId) {

        courseRepository.findById(courseId)
                .filter(Course::getActive)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Course not found with id: " + courseId
                        )
                );

        return batchRepository.findByCourseId(courseId)
                .stream()
                .map(BatchMapper::toResponse)
                .toList();
    }

    @Override
    public BatchResponse createBatch(BatchRequest request) {

        validateBatch(request);

        if (batchRepository.existsByBatchCode(request.batchCode())) {
            throw new BusinessRuleException(
                    "Batch code already exists: " + request.batchCode()
            );
        }

        Course course = courseRepository.findById(request.courseId())
                .filter(Course::getActive)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Active course not found with id: "
                                        + request.courseId()
                        )
                );

        Batch batch = BatchMapper.toEntity(request);

        batch.setCourse(course);

        Batch savedBatch = batchRepository.save(batch);

        return BatchMapper.toResponse(savedBatch);
    }

    @Override
    public BatchResponse updateBatch(
            Long batchId,
            BatchRequest request
    ) {

        validateBatch(request);

        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Batch not found with id: " + batchId
                        )
                );

        batchRepository.findByBatchCode(request.batchCode())
                .filter(existing -> !existing.getId().equals(batchId))
                .ifPresent(existing -> {
                    throw new BusinessRuleException(
                            "Batch code already exists: "
                                    + request.batchCode()
                    );
                });

        Course course = courseRepository.findById(request.courseId())
                .filter(Course::getActive)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Active course not found with id: "
                                        + request.courseId()
                        )
                );

        BatchMapper.updateEntity(batch, request);

        batch.setCourse(course);

        Batch updatedBatch = batchRepository.save(batch);

        return BatchMapper.toResponse(updatedBatch);
    }

    private void validateBatch(BatchRequest request) {

        if (request.startDate().isBefore(LocalDate.now())) {
            throw new BusinessRuleException(
                    "Batch start date cannot be in the past"
            );
        }

        if (request.endDate().isBefore(request.startDate())) {
            throw new BusinessRuleException(
                    "Batch end date cannot be before start date"
            );
        }

        if (request.availableSeats() > request.totalSeats()) {
            throw new BusinessRuleException(
                    "Available seats cannot exceed total seats"
            );
        }

        if (request.availableSeats() < 0) {
            throw new BusinessRuleException(
                    "Available seats cannot be negative"
            );
        }
    }
}
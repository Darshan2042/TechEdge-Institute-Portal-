package com.techedge.portal.service.impl;

import com.techedge.portal.dto.request.PlacementRequest;
import com.techedge.portal.dto.response.PageResponse;
import com.techedge.portal.dto.response.PlacementResponse;
import com.techedge.portal.dto.response.PlacementStatsResponse;
import com.techedge.portal.entity.Company;
import com.techedge.portal.entity.Course;
import com.techedge.portal.entity.Placement;
import com.techedge.portal.entity.Student;
import com.techedge.portal.exception.ResourceNotFoundException;
import com.techedge.portal.mapper.PlacementMapper;
import com.techedge.portal.repository.CompanyRepository;
import com.techedge.portal.repository.CourseRepository;
import com.techedge.portal.repository.PlacementRepository;
import com.techedge.portal.repository.PlacementStatsProjection;
import com.techedge.portal.repository.StudentRepository;
import com.techedge.portal.service.PlacementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class PlacementServiceImpl implements PlacementService {

    private final PlacementRepository placementRepository;
    private final StudentRepository studentRepository;
    private final CompanyRepository companyRepository;
    private final CourseRepository courseRepository;

    public PlacementServiceImpl(
            PlacementRepository placementRepository,
            StudentRepository studentRepository,
            CompanyRepository companyRepository,
            CourseRepository courseRepository
    ) {
        this.placementRepository = placementRepository;
        this.studentRepository = studentRepository;
        this.companyRepository = companyRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PlacementResponse> getPlacements(
            Long companyId,
            Long courseId,
            Integer year,
            BigDecimal minPackage,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Placement> placements = placementRepository.findFiltered(
                companyId,
                courseId,
                year,
                minPackage,
                pageable
        );

        return new PageResponse<>(
                placements.getContent()
                        .stream()
                        .map(PlacementMapper::toResponse)
                        .toList(),
                placements.getNumber(),
                placements.getSize(),
                placements.getTotalElements(),
                placements.getTotalPages(),
                placements.isLast()
        );
    }

    @Override
    public PlacementResponse createPlacement(PlacementRequest request) {

        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found: " + request.studentId()
                        ));

        Company company = companyRepository.findById(request.companyId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Company not found: " + request.companyId()
                        ));

        Course course = null;

        if (request.courseId() != null) {
            course = courseRepository.findById(request.courseId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Course not found: " + request.courseId()
                            ));
        }

        Placement placement = new Placement();

        placement.setStudent(student);
        placement.setCompany(company);
        placement.setCourse(course);
        placement.setJobTitle(request.jobTitle());
        placement.setPackageLpa(request.packageLpa());
        placement.setPlacedOn(request.placedOn());

        return PlacementMapper.toResponse(
                placementRepository.save(placement)
        );
    }

    @Override
    public PlacementResponse updatePlacement(
            Long placementId,
            PlacementRequest request
    ) {
        Placement placement = placementRepository.findById(placementId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Placement not found: " + placementId
                        ));

        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found: " + request.studentId()
                        ));

        Company company = companyRepository.findById(request.companyId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Company not found: " + request.companyId()
                        ));

        Course course = null;

        if (request.courseId() != null) {
            course = courseRepository.findById(request.courseId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Course not found: " + request.courseId()
                            ));
        }

        placement.setStudent(student);
        placement.setCompany(company);
        placement.setCourse(course);
        placement.setJobTitle(request.jobTitle());
        placement.setPackageLpa(request.packageLpa());
        placement.setPlacedOn(request.placedOn());

        return PlacementMapper.toResponse(
                placementRepository.save(placement)
        );
    }

    @Override
    public void deletePlacement(Long placementId) {

        Placement placement = placementRepository.findById(placementId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Placement not found: " + placementId
                        ));

        placementRepository.delete(placement);
    }

    @Override
    @Transactional(readOnly = true)
    public PlacementStatsResponse getStats() {

        PlacementStatsProjection stats =
                placementRepository.getPlacementStats();

        return new PlacementStatsResponse(
                stats.getTotalPlaced(),
                stats.getHiringPartners(),
                stats.getHighestPackageLpa(),
                stats.getAveragePackageLpa(),
                stats.getPlacementsThisYear()
        );
    }
}
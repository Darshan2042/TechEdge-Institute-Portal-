package com.techedge.portal.service.impl;

import com.techedge.portal.entity.Course;
import com.techedge.portal.entity.CourseOffer;
import com.techedge.portal.entity.id.CourseOfferId;
import com.techedge.portal.entity.Offer;
import com.techedge.portal.exception.BusinessRuleException;
import com.techedge.portal.exception.ResourceNotFoundException;
import com.techedge.portal.repository.CourseOfferRepository;
import com.techedge.portal.repository.CourseRepository;
import com.techedge.portal.repository.OfferRepository;
import com.techedge.portal.service.CourseOfferService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CourseOfferServiceImpl implements CourseOfferService {

    private final CourseOfferRepository courseOfferRepository;
    private final CourseRepository courseRepository;
    private final OfferRepository offerRepository;

    public CourseOfferServiceImpl(
            CourseOfferRepository courseOfferRepository,
            CourseRepository courseRepository,
            OfferRepository offerRepository) {

        this.courseOfferRepository = courseOfferRepository;
        this.courseRepository = courseRepository;
        this.offerRepository = offerRepository;
    }

    @Override
    public void linkOffer(Long courseId, Long offerId) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Course not found"));

        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Offer not found"));

        if (!course.getActive()) {
            throw new BusinessRuleException("Cannot link offer to an inactive course");
        }

        if (!offer.getActive()) {
            throw new BusinessRuleException("Cannot link an inactive offer");
        }

        if (courseOfferRepository.existsByCourseIdAndOfferId(courseId, offerId)) {
            throw new BusinessRuleException("Offer is already linked to this course");
        }

        CourseOfferId id = new CourseOfferId(courseId, offerId);

        CourseOffer courseOffer = new CourseOffer();
        courseOffer.setId(id);
        courseOffer.setCourse(course);
        courseOffer.setOffer(offer);

        courseOfferRepository.save(courseOffer);
    }

    @Override
    public void unlinkOffer(Long courseId, Long offerId) {

        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course not found");
        }

        if (!offerRepository.existsById(offerId)) {
            throw new ResourceNotFoundException("Offer not found");
        }

        if (!courseOfferRepository.existsByCourseIdAndOfferId(courseId, offerId)) {
            throw new ResourceNotFoundException(
                    "Offer is not linked to this course"
            );
        }

        courseOfferRepository.deleteById(
                new CourseOfferId(courseId, offerId)
        );
    }
}
package com.techedge.portal.service;

public interface CourseOfferService {

    void linkOffer(Long courseId, Long offerId);

    void unlinkOffer(Long courseId, Long offerId);
}
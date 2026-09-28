package com.techedge.portal.service;

import com.techedge.portal.dto.request.OfferRequest;
import com.techedge.portal.dto.response.OfferResponse;

import java.util.List;

public interface OfferService {

    List<OfferResponse> getLiveOffers();

    List<OfferResponse> getAllOffers();

    OfferResponse createOffer(OfferRequest request);

    OfferResponse updateOffer(
            Long offerId,
            OfferRequest request
    );

    void deactivateOffer(Long offerId);
}
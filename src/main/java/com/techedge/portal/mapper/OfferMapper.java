package com.techedge.portal.mapper;

import com.techedge.portal.dto.request.OfferRequest;
import com.techedge.portal.dto.response.OfferResponse;
import com.techedge.portal.entity.Offer;

public class OfferMapper {

    private OfferMapper() {
    }

    public static Offer toEntity(OfferRequest request) {
        Offer offer = new Offer();

        offer.setTitle(request.title());
        offer.setDescription(request.description());
        offer.setDiscountPercent(request.discountPercent());
        offer.setCouponCode(request.couponCode());
        offer.setValidFrom(request.validFrom());
        offer.setValidTo(request.validTo());

        return offer;
    }

    public static void updateEntity(Offer offer, OfferRequest request) {
        offer.setTitle(request.title());
        offer.setDescription(request.description());
        offer.setDiscountPercent(request.discountPercent());
        offer.setCouponCode(request.couponCode());
        offer.setValidFrom(request.validFrom());
        offer.setValidTo(request.validTo());
    }

    public static OfferResponse toResponse(Offer offer) {
        return new OfferResponse(
                offer.getId(),
                offer.getTitle(),
                offer.getDescription(),
                offer.getDiscountPercent(),
                offer.getCouponCode(),
                offer.getValidFrom(),
                offer.getValidTo()
        );
    }
}
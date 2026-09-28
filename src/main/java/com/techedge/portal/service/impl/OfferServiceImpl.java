package com.techedge.portal.service.impl;

import com.techedge.portal.dto.request.OfferRequest;
import com.techedge.portal.dto.response.OfferResponse;
import com.techedge.portal.entity.Offer;
import com.techedge.portal.exception.BusinessRuleException;
import com.techedge.portal.exception.ResourceNotFoundException;
import com.techedge.portal.mapper.OfferMapper;
import com.techedge.portal.repository.OfferRepository;
import com.techedge.portal.service.OfferService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class OfferServiceImpl implements OfferService {

    private final OfferRepository offerRepository;

    public OfferServiceImpl(
            OfferRepository offerRepository
    ) {
        this.offerRepository = offerRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OfferResponse> getLiveOffers() {

        return offerRepository.findLiveOffers(LocalDate.now())
                .stream()
                .map(OfferMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OfferResponse> getAllOffers() {

        return offerRepository.findAll()
                .stream()
                .map(OfferMapper::toResponse)
                .toList();
    }

    @Override
    public OfferResponse createOffer(
            OfferRequest request
    ) {

        validateOfferDates(request);

        if (request.couponCode() != null
                && !request.couponCode().isBlank()
                && offerRepository.existsByCouponCode(
                request.couponCode()
        )) {

            throw new BusinessRuleException(
                    "Coupon code already exists: "
                            + request.couponCode()
            );
        }

        Offer offer = OfferMapper.toEntity(request);

        offer.setActive(true);

        Offer savedOffer = offerRepository.save(offer);

        return OfferMapper.toResponse(savedOffer);
    }

    @Override
    public OfferResponse updateOffer(
            Long offerId,
            OfferRequest request
    ) {

        validateOfferDates(request);

        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Offer not found with id: " + offerId
                        )
                );

        if (request.couponCode() != null
                && !request.couponCode().isBlank()) {

            offerRepository.findByCouponCode(
                            request.couponCode()
                    )
                    .filter(existing ->
                            !existing.getId().equals(offerId)
                    )
                    .ifPresent(existing -> {
                        throw new BusinessRuleException(
                                "Coupon code already exists: "
                                        + request.couponCode()
                        );
                    });
        }

        OfferMapper.updateEntity(offer, request);

        Offer updatedOffer = offerRepository.save(offer);

        return OfferMapper.toResponse(updatedOffer);
    }

    @Override
    public void deactivateOffer(Long offerId) {

        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Offer not found with id: " + offerId
                        )
                );

        offer.setActive(false);

        offerRepository.save(offer);
    }

    private void validateOfferDates(
            OfferRequest request
    ) {

        if (request.validTo().isBefore(request.validFrom())) {
            throw new BusinessRuleException(
                    "Offer valid-to date cannot be before valid-from date"
            );
        }
    }
}
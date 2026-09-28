package com.techedge.portal.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OfferResponse(
        Long id,
        String title,
        String description,
        BigDecimal discountPercent,
        String couponCode,
        LocalDate validFrom,
        LocalDate validTo
) {
}
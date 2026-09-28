package com.techedge.portal.dto.response;

import java.math.BigDecimal;

public record PlacementStatsResponse(

        Long totalPlaced,

        Long hiringPartners,

        BigDecimal highestPackageLpa,

        BigDecimal averagePackageLpa,

        Long placementsThisYear
) {
}
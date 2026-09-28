package com.techedge.portal.repository;

import java.math.BigDecimal;

public interface PlacementStatsProjection {

    Long getTotalPlaced();

    Long getHiringPartners();

    BigDecimal getHighestPackageLpa();

    BigDecimal getAveragePackageLpa();

    Long getPlacementsThisYear();
}
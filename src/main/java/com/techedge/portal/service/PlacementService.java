package com.techedge.portal.service;

import com.techedge.portal.dto.request.PlacementRequest;
import com.techedge.portal.dto.response.PageResponse;
import com.techedge.portal.dto.response.PlacementResponse;
import com.techedge.portal.dto.response.PlacementStatsResponse;

import java.math.BigDecimal;

public interface PlacementService {

    PageResponse<PlacementResponse> getPlacements(
            Long companyId,
            Long courseId,
            Integer year,
            BigDecimal minPackage,
            int page,
            int size
    );

    PlacementResponse createPlacement(PlacementRequest request);

    PlacementResponse updatePlacement(
            Long placementId,
            PlacementRequest request
    );

    void deletePlacement(Long placementId);

    PlacementStatsResponse getStats();
}
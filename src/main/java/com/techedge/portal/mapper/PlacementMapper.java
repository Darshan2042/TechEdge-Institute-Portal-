package com.techedge.portal.mapper;

import com.techedge.portal.dto.response.PlacementResponse;
import com.techedge.portal.entity.Placement;

public class PlacementMapper {

    private PlacementMapper() {
    }

    public static PlacementResponse toResponse(Placement placement) {

        return new PlacementResponse(
                placement.getId(),
                placement.getStudent().getUser().getFullName(),
                placement.getCourse() != null
                        ? placement.getCourse().getTitle()
                        : null,
                placement.getCompany().getName(),
                placement.getCompany().getLogoUrl(),
                placement.getJobTitle(),
                placement.getPackageLpa(),
                placement.getPlacedOn()
        );
    }
}
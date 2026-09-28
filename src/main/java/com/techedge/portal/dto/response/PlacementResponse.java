package com.techedge.portal.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PlacementResponse(

        Long id,

        String studentName,

        String courseTitle,

        String companyName,

        String companyLogoUrl,

        String jobTitle,

        BigDecimal packageLpa,

        LocalDate placedOn
) {
}
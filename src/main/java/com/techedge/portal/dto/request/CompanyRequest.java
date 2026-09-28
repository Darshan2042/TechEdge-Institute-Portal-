package com.techedge.portal.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CompanyRequest(

        @NotBlank(message = "Company name is required")
        String name,

        String industry,

        String website,

        String logoUrl
) {
}
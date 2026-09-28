package com.techedge.portal.dto.response;

public record CompanyResponse(
        Long id,
        String name,
        String industry,
        String website,
        String logoUrl
) {
}
package com.techedge.portal.dto.response;

public record TopicResponse(
        Long id,
        String title,
        Integer sortOrder
) {
}
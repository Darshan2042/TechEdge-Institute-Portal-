package com.techedge.portal.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record QuestionRequest(

        @NotBlank(message = "Question text is required")
        String questionText,

        @NotNull(message = "Marks are required")
        @Min(value = 1, message = "Marks must be at least 1")
        Integer marks,

        @NotEmpty(message = "Exactly 4 options are required")
        @Size(min = 4, max = 4, message = "Exactly 4 options are required")
        @Valid
        List<OptionRequest> options

) {

    public record OptionRequest(

            @NotBlank(message = "Option text is required")
            String optionText,

            @NotNull(message = "Correct flag is required")
            Boolean correct

    ) {}
}
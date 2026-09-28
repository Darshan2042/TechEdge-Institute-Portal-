package com.techedge.portal.dto.request;

import jakarta.validation.constraints.*;

public record RegisterRequest(

        @NotBlank
        @Size(min = 3, max = 100)
        String fullName,

        @NotBlank
        @Email
        String email,

        @NotBlank
        @Size(min = 8)
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z\\d]).+$",
                message = "Password must contain at least one letter, one digit and one special character"
        )
        String password,

        @NotBlank
        @Pattern(
                regexp = "\\d{10}",
                message = "Phone must contain exactly 10 digits"
        )
        String phone,

        @Size(max = 100)
        String qualification,

        @Min(1980)
        Integer graduationYear,

        @Size(max = 80)
        String city
) {
}
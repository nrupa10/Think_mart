package com.thinkmart.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContactRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Email @Size(max = 190) String email,
        @NotBlank @Size(max = 160) String subject,
        @NotBlank @Size(min = 10, max = 5000) String message) {
}
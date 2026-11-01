package com.thinkmart.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(max = 100) String fullName,
        @NotBlank @Email @Size(max = 190) String email,
        @NotBlank @Pattern(regexp = "^[+0-9() .-]{7,30}$") String phone,
        @NotBlank @Size(min = 10, max = 72) String password) {
}
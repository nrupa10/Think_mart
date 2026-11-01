package com.thinkmart.dto;

public record AuthResponse(String token, UserResponse user) {
}
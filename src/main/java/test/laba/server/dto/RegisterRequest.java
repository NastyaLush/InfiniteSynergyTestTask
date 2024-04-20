package test.laba.server.dto;

public record RegisterRequest(
        String email,
        String password
) {
}
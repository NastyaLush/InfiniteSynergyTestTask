package test.laba.server.dto;

public record AuthenticationRequest(
        String email,
        String password
) {
}
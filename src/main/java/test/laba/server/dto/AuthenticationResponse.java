package test.laba.server.dto;
import lombok.Builder;

@Builder
public record AuthenticationResponse(
        String accessToken
) {
}
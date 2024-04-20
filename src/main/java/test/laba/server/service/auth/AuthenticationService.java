package test.laba.server.service.auth;


import java.util.List;
import lombok.RequiredArgsConstructor;
import test.laba.server.repository.AuthRepository;
import test.laba.server.dto.AuthenticationRequest;
import test.laba.server.dto.AuthenticationResponse;
import test.laba.server.dto.RegisterRequest;
import test.laba.server.entity.Token;
import test.laba.server.repository.TokenRepository;
import test.laba.server.entity.User;
import test.laba.server.repository.UserRepository;
import test.laba.server.util.PasswordHandler;


@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final AuthRepository authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        try {
            var user = User.builder()
                           .email(request.email())
                           .password(PasswordHandler.encryptPass(request.password()))
                           .tokens(List.of())
                           .amount(1000L)
                           .build();
            var savedUser = repository.save(user);
            var jwtToken = jwtService.generateToken(user);
            saveUserToken(savedUser, jwtToken);
            return AuthenticationResponse.builder()
                                         .accessToken(jwtToken)
                                         .build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authentificate(
                request.email(),
                PasswordHandler.encryptPass(request.password())
        );
        var user = repository.findByEmail(request.email())
                             .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                                     .accessToken(jwtToken)
                                     .build();
    }

    public boolean authenticate(String email) {
        return authenticationManager.authentificate(
                email
        );
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                         .user(user)
                         .tokenValue(jwtToken)
                         .expired(false)
                         .revoked(false)
                         .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.updateAll(validUserTokens);
    }
}

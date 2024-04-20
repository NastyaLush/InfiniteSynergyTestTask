package test.laba.server.repository;

import java.util.List;
import java.util.Optional;
import test.laba.server.entity.Token;


public interface TokenRepository {
    List<Token> findAllValidTokenByUser(Long id);

    Optional<Token> findByTokenValue(String token);

    void save(Token storedToken);

    void updateAll(List<Token> validUserTokens);
}

package test.laba.server.repository;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import test.laba.server.entity.Token;


@RequiredArgsConstructor
public class TokenRepositoryImpl implements TokenRepository {
    private final SessionFactory sessionFactory;

    @Override
    public List<Token> findAllValidTokenByUser(Long id) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Query<Token> query = session.createQuery("FROM Token WHERE user.id = :id", Token.class);
        query.setParameter("id", id);
        List<Token> token1 = query.list();

        session.getTransaction()
               .commit();
        session.close();

        return token1;
    }

    @Override
    public Optional<Token> findByTokenValue(String token) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Query<Token> query = session.createQuery("FROM Token WHERE tokenValue = :token", Token.class);
        query.setParameter("token", token);
        Token token1 = query.uniqueResult();

        session.getTransaction()
               .commit();
        session.close();

        return Optional.ofNullable(token1);
    }

    @Override
    public void save(Token storedToken) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.persist(storedToken);
        session.getTransaction()
               .commit();
        session.close();
    }

    @Override
    public void updateAll(List<Token> validUserTokens) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        for (Token token : validUserTokens) {
            session.update(token);
        }
        session.getTransaction()
               .commit();
        session.close();
    }
}

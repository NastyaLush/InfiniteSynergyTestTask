package test.laba.server.repository;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import test.laba.server.entity.User;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final SessionFactory sessionFactory;

    @Override
    public Optional<User> findByEmail(String email) {

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Query<User> query = session.createQuery("FROM User WHERE email = :email", User.class);
        query.setParameter("email", email);
        User user = query.uniqueResult();

        session.getTransaction()
               .commit();
        session.close();

        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> findById(Long id) {

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        User user = session.get(User.class, id);

        session.getTransaction()
               .commit();
        session.close();

        return Optional.ofNullable(user);

    }

    @Override
    public User save(User user) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.save(user);
        session.getTransaction()
               .commit();
        session.close();
        return user;
    }

    @Override
    public void sendMoney(String fromEmail, String toEmail, Long amount) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            User fromUser = session.createQuery("FROM User WHERE email = :email", User.class)
                                   .setParameter("email", fromEmail)
                                   .uniqueResult();

            if (fromUser == null) {
                throw new IllegalArgumentException("User with email " + fromEmail + " does not exist");
            }
            if (fromUser.getAmount() - amount < 0) {
                throw new IllegalArgumentException("new amount for " + fromEmail + " is negative current amount is " + fromUser.getAmount());
            }

            fromUser.setAmount(fromUser.getAmount() - amount);
            session.update(fromUser);

            User toUser = session.createQuery("FROM User WHERE email = :email", User.class)
                                 .setParameter("email", toEmail)
                                 .uniqueResult();

            if (toUser == null) {
                throw new IllegalArgumentException("User with email " + toEmail + " does not exist");
            }

            toUser.setAmount(toUser.getAmount() + amount);
            session.update(toUser);

            session.getTransaction()
                   .commit();
        }
    }
}

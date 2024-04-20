package test.laba.server.repository;

import java.util.Optional;
import test.laba.server.entity.User;

public interface UserRepository{

    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);

    User save(User user);
    void sendMoney(String fromEmail,String toEmail, Long amount);
}

package test.laba.server.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import test.laba.server.entity.User;
import test.laba.server.repository.UserRepository;

@RequiredArgsConstructor
public class MoneyService {
    private final UserRepository userRepository;

    public Long get(String email) {
        Optional<User> byEmail = userRepository.findByEmail(email);
        return byEmail.map(User::getAmount)
                      .orElse(0L);
    }

    public void send(String fromEmail, String toEmail, Long amount) {
        userRepository.sendMoney(fromEmail, toEmail, amount);
    }
}

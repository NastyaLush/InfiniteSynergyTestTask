package test.laba.server.repository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthRepository {
    private final UserRepository userRepository;

    public void authentificate(String username, String password) {
        userRepository.findByEmail(username)
                      .ifPresent(user -> {
                          if (!user.getPassword()
                                   .equals(password)) {
                              throw new IllegalArgumentException("password is incorrect");
                          }
                      });
    }

    public boolean authentificate(String username) {
        return userRepository.findByEmail(username)
                             .isPresent();
    }
}

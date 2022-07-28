package io.everyonecodes.cryptolog.service;

import io.everyonecodes.cryptolog.data.User;

import java.security.Principal;
import java.util.Optional;

public interface UserService {

    void saveUser(User user);
    void save(User user);
    void saveAdmin(User user);

    boolean isUserAlreadyPresent(User user);

    Optional<User> findUserByResetToken(String token);

    Optional<User> findUserByEmail(String userEmail);

    User loadLoggedInUser(Principal principal);

    boolean isUserValid(User user);

    void deleteUserByEmail(User user);

}

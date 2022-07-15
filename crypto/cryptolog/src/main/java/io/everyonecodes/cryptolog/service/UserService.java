package io.everyonecodes.cryptolog.service;

import io.everyonecodes.cryptolog.data.User;

import java.util.Optional;

public interface UserService {

	public void saveUser(User user);
	
	public boolean isUserAlreadyPresent(User user);

    Optional<User> findUserByResetToken(String token);

	Optional<User> findUserByEmail(String userEmail);
}

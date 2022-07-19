package io.everyonecodes.cryptolog.service;

import io.everyonecodes.cryptolog.data.User;

import java.util.Optional;

public interface UserService {

	void saveUser(User user);

	void reSaveUser(User user);
	
	 boolean isUserAlreadyPresent(User user);

    Optional<User> findUserByResetToken(String token);

	Optional<User> findUserByEmail(String userEmail);

	 boolean isUserValid(User user);

	 void deleteUserByEmail(User user);
}

package io.everyonecodes.cryptolog.service;

import io.everyonecodes.cryptolog.data.User;

public interface UserService {

	public void saveUser(User user);
	
	public boolean isUserAlreadyPresent(User user);
}

package io.everyonecodes.registration.service;

import io.everyonecodes.registration.data.User;

public interface UserService {

	public void saveUser(User user);
	
	public boolean isUserAlreadyPresent(User user);
}

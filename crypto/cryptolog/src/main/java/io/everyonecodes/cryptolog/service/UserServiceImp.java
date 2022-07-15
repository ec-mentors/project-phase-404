package io.everyonecodes.cryptolog.service;

import io.everyonecodes.cryptolog.data.Role;
import io.everyonecodes.cryptolog.data.User;
import io.everyonecodes.cryptolog.repository.UserRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class UserServiceImp implements UserService {
private final PasswordEncoder encoder;
//private final RoleRepository roleRepository;
private final UserRepository userRepository;
private final JavaMailSender mailSender;

    public UserServiceImp(PasswordEncoder encoder, /*RoleRepository roleRepository,*/ UserRepository userRepository, JavaMailSender mailSender) {
        this.encoder = encoder;
        //this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    @Override
    public void saveUser(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        user.setStatus("NOT_VERIFIED");
        //Role userRole = roleRepository.findByRole("SITE_USER");
        Role userRole = new Role("SITE_USER");
        user.setRoles(new HashSet<>(List.of(userRole)));
        userRepository.save(user);
    }

    @Override
    public boolean isUserAlreadyPresent(User user) {
        //return userRepository.findByEmail(user.getEmail()).isPresent();
        return userRepository.existsByEmail(user.getEmail());
    }

}

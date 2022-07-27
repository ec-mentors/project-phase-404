package io.everyonecodes.cryptolog.service;

import io.everyonecodes.cryptolog.CoingeckoClient;
import io.everyonecodes.cryptolog.data.Coin;
import io.everyonecodes.cryptolog.data.Role;
import io.everyonecodes.cryptolog.data.User;
import io.everyonecodes.cryptolog.repository.RoleRepository;
import io.everyonecodes.cryptolog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;

@Service
public class UserServiceImp implements UserService {
    private final PasswordEncoder encoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CoingeckoClient coingeckoClient;
    
    private final String roleName;
    private final String roleDescription;

    public UserServiceImp(PasswordEncoder encoder, RoleRepository roleRepository, UserRepository userRepository, CoingeckoClient coingeckoClient, @Value("${messages.user.userRole.name}") String roleName, @Value("${messages.user.userRole.description}") String roleDescription) {
        this.encoder = encoder;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.coingeckoClient = coingeckoClient;
        this.roleName = roleName;
        this.roleDescription = roleDescription;
    }

    @Override
    public void saveUser(User user) {
        if (!isUserAlreadyPresent(user)) {

            prepareUserDetails(user);
            user.setVerified(false);

        }
        userRepository.save(user);
    }
    @Override
    public void saveAdmin(User user) {
        if (!isUserAlreadyPresent(user)) {
 
            prepareUserDetails(user);
            user.setVerified(true);
        }
        
        userRepository.save(user);
    }
    
    private void prepareUserDetails(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        Role userRole = roleRepository.findByName(roleName);
        if (userRole == null) {
            userRole = new Role(roleName, roleDescription);
        }
        
        user.setRoles(new HashSet<>(List.of(userRole)));
    }

    public boolean hasAllTier (User user) {
        Set<String> userCoinIds =  user.getCoinIds();
        List<Coin> userCoins = coingeckoClient.getCoinsById(userCoinIds);
        List<Integer> userNumbers = new ArrayList<>();
        int numberTwo=0;
        int numberThree=0;

        for(Coin coin : userCoins) {
            int number = coin.getMarket_cap_rank();
            if(number > 2 && number <=50) {
                numberTwo++;
            }
            if(number > 50) {
                numberThree++;
            }
        }

        return (numberThree > 0 && numberTwo >0);
    }
    public boolean hasTierTwo (User user) {
        Set<String> userCoinIds =  user.getCoinIds();
        List<Coin> userCoins = coingeckoClient.getCoinsById(userCoinIds);
        List<Integer> userNumbers = new ArrayList<>();
        int numberTwo=0;
        int numberThree=0;

        for(Coin coin : userCoins) {
            int number = coin.getMarket_cap_rank();
            if(number > 2 && number <=50) {
                numberTwo++;
            }

        }

        return  numberTwo >0 ;
    }

    @Override
    public boolean isUserAlreadyPresent(User user) {
        return userRepository.existsByEmail(user.getEmail());
    }

    @Override
    public Optional<User> findUserByResetToken(String token) {
        return userRepository.findUserByResetToken(token);
    }

    @Override
    public Optional<User> findUserByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail);
    }

    @Override
    public User loadLoggedInUser(Principal principal) {
        String userName = principal.getName();
        return findUserByEmail(userName).orElse(null);
    }

    @Override
    public boolean isUserValid(User user) {
        var oUser =  userRepository.findByEmail(user.getEmail());
        return oUser.map(User::isVerified).orElse(false);
    }

    @Override
    public void deleteUserByEmail(User user) {
        Optional<User> oUser = userRepository.findByEmail(user.getEmail());
        if (oUser.isPresent()){
            userRepository.delete(user);
        }
    }
}

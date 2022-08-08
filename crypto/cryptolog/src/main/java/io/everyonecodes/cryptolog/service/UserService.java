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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class  UserService {
    private final PasswordEncoder encoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CoingeckoClient coingeckoClient;
    
    private final String roleName;
    private final String roleDescription;
    private final String assetsAllocation;
    
    
    public UserService(PasswordEncoder encoder, RoleRepository roleRepository, UserRepository userRepository, CoingeckoClient coingeckoClient, @Value("${messages.user.userRole.name}") String roleName, @Value("${messages.user.userRole.description}") String roleDescription, @Value("${messages.user.userRole.assetsAllocation}") String assetsAllocation) {
        this.encoder = encoder;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.coingeckoClient = coingeckoClient;
        this.roleName = roleName;
        this.roleDescription = roleDescription;
        this.assetsAllocation = assetsAllocation;
    }
    
    
    
    public void saveUser(User user) {
        if (!isUserAlreadyPresent(user)) {
            
            prepareUserDetails(user);
            user.setVerified(false);
            
        }
        user.setAssetsAllocation(assetsAllocation);
        userRepository.save(user);
    }
    
    public void saveAdmin(User user) {
        if (!isUserAlreadyPresent(user)) {
            
            prepareUserDetails(user);
            user.setVerified(true);
        }
        
        userRepository.save(user);
    }
    
    
    public void save(User user) {
        
        userRepository.save(user);
    }
    
    private void prepareUserDetails(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        user.setAssetsAllocation(assetsAllocation);
        Role userRole = roleRepository.findByName(roleName);
        if (userRole == null) {
            userRole = new Role(roleName, roleDescription);
        }
        
        user.setRoles(new HashSet<>(List.of(userRole)));
    }
    
    public boolean hasAllTier (User user) {
        return hasTierOne(user) && hasTierTwo(user) && hasTierThree(user);
    }

    public boolean hasTierOne (User user) {
        Set<String> userCoinIds =  user.getCoinIds();
        List<Coin> userCoins = coingeckoClient.getCoinsById(userCoinIds);

        int numberOne = 0;

        for(Coin coin : userCoins) {
            int number = coin.getMarket_cap_rank();
            if(number > 0 && number < 3) {
                numberOne++;
            }

        }

        return  numberOne > 0 ;
    }

    public boolean hasTierTwo (User user) {
        Set<String> userCoinIds =  user.getCoinIds();
        List<Coin> userCoins = coingeckoClient.getCoinsById(userCoinIds);
        
        int numberTwo=0;
        
        for(Coin coin : userCoins) {
            int number = coin.getMarket_cap_rank();
            if(number > 2 && number <=50) {
                numberTwo++;
            }
            
        }
        
        return  numberTwo > 0 ;
    }

    public boolean hasTierThree (User user) {
        Set<String> userCoinIds =  user.getCoinIds();
        List<Coin> userCoins = coingeckoClient.getCoinsById(userCoinIds);

        int numberThree = 0;

        for(Coin coin : userCoins) {
            int number = coin.getMarket_cap_rank();
            if(number > 50) {
                numberThree++;
            }

        }

        return  numberThree > 0 ;
    }
    
    public boolean hasRole(User user, String name) {
        List<String> roles = user.getRoles().stream().map(Role :: getName).toList();
        return roles.contains(name);
    }
    
    public boolean isUserAlreadyPresent(User user) {
        return userRepository.existsByEmail(user.getEmail());
    }
    
    
    public Optional<User> findUserByResetToken(String token) {
        return userRepository.findUserByResetToken(token);
    }
    
    
    public Optional<User> findUserByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail);
    }
    
    
    public User loadLoggedInUser(Principal principal) {
        String userName = principal.getName();
        return findUserByEmail(userName).orElse(null);
    }
    
    
    public boolean isUserValid(User user) {
        var oUser =  userRepository.findByEmail(user.getEmail());
        return oUser.map(User::isVerified).orElse(false);
    }
    
    
    public void deleteUserByEmail(User user) {
        Optional<User> oUser = userRepository.findByEmail(user.getEmail());
        if (oUser.isPresent()){
            userRepository.delete(user);
        }
    }
}







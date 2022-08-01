package io.everyonecodes.cryptolog.service;

import io.everyonecodes.cryptolog.data.User;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class AssetsAllocationService {


    private final UserService userService;

    public AssetsAllocationService(UserService userService) {
        this.userService = userService;
    }


    public void saveAsset(String assetsAllocation, Principal principal) {
        User user = userService.loadLoggedInUser(principal);
        user.setAssetsAllocation(assetsAllocation);
         userService.save(user);
    }

}

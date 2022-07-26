package io.everyonecodes.cryptolog.service;

import io.everyonecodes.cryptolog.data.AssetsAllocation;
import io.everyonecodes.cryptolog.data.User;
import io.everyonecodes.cryptolog.repository.AssetsAllocationRepository;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class AssetsAllocationService {

    private final AssetsAllocationRepository assetsAllocationRepository;
    private final UserService userService;
    public AssetsAllocationService(AssetsAllocationRepository assetsAllocationRepository, UserService userService) {
        this.assetsAllocationRepository = assetsAllocationRepository;
        this.userService = userService;
    }

    public void saveAsset(AssetsAllocation assetsAllocation, Principal principal) {
        User user = userService.loadLoggedInUser(principal);
        user.setAssetsAllocation(assetsAllocation);
         userService.saveAdmin(user);
    }

}

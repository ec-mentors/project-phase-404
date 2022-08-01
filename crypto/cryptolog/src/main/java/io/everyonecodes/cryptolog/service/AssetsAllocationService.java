package io.everyonecodes.cryptolog.service;

import io.everyonecodes.cryptolog.data.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.security.Principal;

@Service
public class AssetsAllocationService {


    private final UserService userService;
    private final UserServiceImp userServiceImp;

    private final String maximalist;
    private final String conservative;
    private final String gambler;

    private final String emptyPortfolio;
    private final String maximalistInfo;
    private final String missingCoinsConserv;
    private final String missingCoinsGambler;

    public AssetsAllocationService(UserService userService, UserServiceImp userServiceImp,
                                   @Value("${assets.maximalist}") String maximalist,
                                   @Value("${assets.conservative}") String conservative,
                                   @Value("${assets.gambler}") String gambler,
                                   @Value("${messages.asset.emptyPortfolio}") String emptyPortfolio,
                                   @Value("${messages.asset.maximalistInfo}") String maximalistInfo,
                                   @Value("${messages.asset.missingCoinsConserv}") String missingCoinsConserv,
                                   @Value("${messages.asset.missingCoinsGambler}") String missingCoinsGambler) {
        this.userService = userService;
        this.userServiceImp = userServiceImp;
        this.maximalist = maximalist;
        this.conservative = conservative;
        this.gambler = gambler;
        this.emptyPortfolio = emptyPortfolio;
        this.maximalistInfo = maximalistInfo;
        this.missingCoinsConserv = missingCoinsConserv;
        this.missingCoinsGambler = missingCoinsGambler;
    }

    public void saveAsset(String assetsAllocation, Model model, Principal principal) {
        User user = userService.loadLoggedInUser(principal);
        if (user.getCoinIds().isEmpty()) {
            model.addAttribute("assetMessage", emptyPortfolio);
        } else {
            user.setAssetsAllocation(assetsAllocation);

            boolean tierTwo = userServiceImp.hasAllTier(user);
            boolean tierAll = userServiceImp.hasAllTier(user);
            if (user.getAssetsAllocation().equals(maximalist)) {

                userService.saveUser(user);
                model.addAttribute("assetMessage", maximalistInfo);
            }
            if (user.getAssetsAllocation().equals(gambler) && !tierAll) {
                model.addAttribute("assetMessage", missingCoinsGambler);
            }
            if (user.getAssetsAllocation().equals(gambler) && tierAll) {
                userService.saveUser(user);

            }
            if (user.getAssetsAllocation().equals(conservative) && !tierTwo) {
                model.addAttribute("assetMessage", missingCoinsConserv);

            }
            if (user.getAssetsAllocation().equals(conservative) && tierTwo) {
                userService.saveUser(user);
            }
        }
    }
}

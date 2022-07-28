package io.everyonecodes.cryptolog.controller;

import io.everyonecodes.cryptolog.CoingeckoClient;
import io.everyonecodes.cryptolog.data.User;
import io.everyonecodes.cryptolog.service.AssetUpdateForm;
import io.everyonecodes.cryptolog.service.UserService;
import io.everyonecodes.cryptolog.service.UserServiceImp;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AssetController {
    private final CoingeckoClient client;
    private final UserService userService;
    private final UserServiceImp userServiceImp;

    private static List<String> assetsAllocations;

    static {
        assetsAllocations = new ArrayList<>();
        assetsAllocations.add("Maximalist");
        assetsAllocations.add("Conservative");
        assetsAllocations.add("Gambler");

    }

    public AssetController(CoingeckoClient client, UserService userService, UserServiceImp userServiceImp) {
        this.client = client;
        this.userService = userService;
        this.userServiceImp = userServiceImp;
    }

    @GetMapping("/asset")
    public String display(Model model) {
        final AssetUpdateForm assetUpdateForm = new AssetUpdateForm();

        model.addAttribute("assetUpdateForm", assetUpdateForm);

        return "asset";
    }


    @PostMapping("/asset")
    public String updateAsset(Principal principal, @ModelAttribute("assetUpdateForm")
            AssetUpdateForm assetUpdateForm, Model model) {
        User user = userService.loadLoggedInUser(principal);
        if (user.getCoinIds().isEmpty()) {
            model.addAttribute("assetMessage", "Missing coins. Please mind that you need to continue adding coins to your portfolio in order to select an asset allocation");
        } else {
            user.setAssetsAllocation(assetUpdateForm.getAssetsAllocation());

            boolean tierTwo = userServiceImp.hasAllTier(user);
            boolean tierAll = userServiceImp.hasAllTier(user);
            if (user.getAssetsAllocation().equals("Maximalist")) {

                userService.saveAdmin(user);
                model.addAttribute("assetMessage", "Please mind that The Maximalist portfolio allocation only takes into account Bitcoin. If you have multiple coins in your portfolio, these will be ignored for any yield calculation");
            }
            if (user.getAssetsAllocation().equals("Gambler") && !tierAll) {
                model.addAttribute("assetMessage", "Missing coin from Tier 2 or 3. Please mind that you need to continue adding coins to your portfolio in order to select this particular asset allocation");

            }
            if (user.getAssetsAllocation().equals("Gambler") && tierAll) {
                userService.saveAdmin(user);

            }

            if (user.getAssetsAllocation().equals("Conservative") && !tierTwo) {
                model.addAttribute("assetMessage", "Missing coin from Tier 2. Please mind that you need to continue adding coins to your portfolio in order to select this particular asset allocation");

            }
            if (user.getAssetsAllocation().equals("Conservative") && tierTwo) {
                userService.saveAdmin(user);

            }
        }
        return "asset";
    }
}

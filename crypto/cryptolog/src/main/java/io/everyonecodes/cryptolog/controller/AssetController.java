package io.everyonecodes.cryptolog.controller;

import io.everyonecodes.cryptolog.data.AssetUpdateForm;
import io.everyonecodes.cryptolog.data.User;
import io.everyonecodes.cryptolog.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class AssetController  {


    private final UserService userService;
    public AssetController( UserService userService) {
        this.userService = userService;

    }
    @GetMapping("/asset")
    public String display(Model model) {
        final AssetUpdateForm assetUpdateForm = new AssetUpdateForm();

        model.addAttribute("assetUpdateForm", assetUpdateForm);

        return "asset";
    }

        @PostMapping(value = "/asset")
        public String updateAsset(Principal principal, @ModelAttribute("assetUpdateForm")
            AssetUpdateForm assetUpdateForm ,Model model  ) {

        User user = userService.loadLoggedInUser(principal);
        if(user.getCoinIds().isEmpty()) {
            model.addAttribute("assetMessage", "Missing coins. Please mind that you need to continue adding coins to your portfolio in order to select an asset allocation");
        }

        else {
            user.setAssetsAllocation(assetUpdateForm.getAssetsAllocation());
            userService.save(user);
            boolean tierTwo = userService.hasTierTwo(user);
            boolean tierAll = userService.hasAllTier(user);
            model.addAttribute("assetMessage", "Please mind that The Maximalist portfolio allocation only takes into account Bitcoin. If you have multiple coins in your portfolio, these will be ignored for any yield calculation");


            if (user.getAssetsAllocation().equals("Gambler") && !tierAll) {

                user.setAssetsAllocation("none");
                userService.save(user);
                model.addAttribute("assetMessage", "Missing coin from Tier 2 or 3. Please mind that you need to continue adding coins to your portfolio in order to select this particular asset allocation");

            }
            if (user.getAssetsAllocation().equals("Gambler") && tierAll) {
                userService.save(user);
            }

            if (user.getAssetsAllocation().equals("Conservative") && !tierTwo) {
                user.setAssetsAllocation("none");
                userService.save(user);
                model.addAttribute("assetMessage", "Missing coin from Tier 2. Please mind that you need to continue adding coins to your portfolio in order to select this particular asset allocation");
            }
            if (user.getAssetsAllocation().equals("Conservative") && tierTwo) {
                userService.save(user);
            }
        }
        return "asset";
    }
}

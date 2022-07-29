package io.everyonecodes.cryptolog.controller;

import io.everyonecodes.cryptolog.CoingeckoClient;
import io.everyonecodes.cryptolog.data.Role;
import io.everyonecodes.cryptolog.data.User;
import io.everyonecodes.cryptolog.repository.RoleRepository;
import io.everyonecodes.cryptolog.data.AssetUpdateForm;
import io.everyonecodes.cryptolog.service.UserService;
import io.everyonecodes.cryptolog.service.UserServiceImp;
import org.springframework.beans.factory.annotation.Value;
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
    private final String roleName;
    private final String roleDescription;
    private final RoleRepository roleRepository;

    private static List<String> assetsAllocations;
    static {
        assetsAllocations = new ArrayList<>();
        assetsAllocations.add("Maximalist");
        assetsAllocations.add("Conservative");
        assetsAllocations.add("Gambler");

    }

    public AssetController(CoingeckoClient client, UserService userService, UserServiceImp userServiceImp,
                           @Value("${messages.user.yieldRole.name}") String roleName, @Value("${messages.user.yieldRole.description}") String roleDescription, RoleRepository roleRepository) {
        this.client = client;
        this.userService = userService;
        this.userServiceImp = userServiceImp;
        this.roleName = roleName;
        this.roleDescription = roleDescription;
        this.roleRepository = roleRepository;
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
        Role yield = new Role(roleName,roleDescription);
        User user = userService.loadLoggedInUser(principal);

        if(user.getCoinIds().isEmpty()) {
            Role roleT = roleRepository.findByName("YIELD_USER");
            user.getRoles().remove(roleT);
            userServiceImp.save(user);
            model.addAttribute("assetMessage", "Missing coins. Please mind that you need to continue adding coins to your portfolio in order to select an asset allocation");
        }

        else {
            user.setAssetsAllocation(assetUpdateForm.getAssetsAllocation());
            userServiceImp.save(user);
            boolean tierTwo = userServiceImp.hasTierTwo(user);
            boolean tierAll = userServiceImp.hasAllTier(user);
            if (user.getAssetsAllocation().equals("Maximalist")) {
                Role roleT = roleRepository.findByName("YIELD_USER");
                if (roleT == null) {
                    user.getRoles().add(yield);
                    userServiceImp.save(user);
                }

                model.addAttribute("assetMessage", "Please mind that The Maximalist portfolio allocation only takes into account Bitcoin. If you have multiple coins in your portfolio, these will be ignored for any yield calculation");
            }


            if (user.getAssetsAllocation().equals("Gambler") && !tierAll) {
                Role roleT = roleRepository.findByName("YIELD_USER");
                user.getRoles().remove(roleT);
                user.setAssetsAllocation("none");
                userServiceImp.save(user);
                model.addAttribute("assetMessage", "Missing coin from Tier 2 or 3. Please mind that you need to continue adding coins to your portfolio in order to select this particular asset allocation");

            }
            if (user.getAssetsAllocation().equals("Gambler") && tierAll) {
                Role roleT = roleRepository.findByName("YIELD_USER");
                if (roleT == null) {
                    user.getRoles().add(yield);
                    userServiceImp.save(user);
                }

            }

            if (user.getAssetsAllocation().equals("Conservative") && !tierTwo) {
                Role roleT = roleRepository.findByName("YIELD_USER");
                user.getRoles().remove(roleT);
                user.setAssetsAllocation("none");
                userServiceImp.save(user);
                model.addAttribute("assetMessage", "Missing coin from Tier 2. Please mind that you need to continue adding coins to your portfolio in order to select this particular asset allocation");

            }
            if (user.getAssetsAllocation().equals("Conservative") && tierTwo) {
                Role roleT = roleRepository.findByName("YIELD_USER");
                if (roleT == null) {
                    user.getRoles().add(yield);
                    userServiceImp.save(user);
                }


            }
        }
        return "asset";
    }
}
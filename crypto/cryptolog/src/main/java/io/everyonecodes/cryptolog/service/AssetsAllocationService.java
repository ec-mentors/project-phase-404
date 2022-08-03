package io.everyonecodes.cryptolog.service;

import io.everyonecodes.cryptolog.CoingeckoClient;
import io.everyonecodes.cryptolog.data.*;
import io.everyonecodes.cryptolog.repository.CustomAssetAllocationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AssetsAllocationService {

    private final UserServiceImp userServiceImp;
    private final CustomAssetAllocationRepository repository;
    private final CoingeckoClient client;

    private final String maximalist;
    private final String conservative;
    private final String gambler;
    private final String custom;

    private final String emptyPortfolio;
    private final String maximalistInfo;
    private final String missingCoinsConserv;
    private final String missingCoinsGambler;

    public AssetsAllocationService(UserServiceImp userServiceImp,
                                   CustomAssetAllocationRepository repository,
                                   CoingeckoClient client,
                                   @Value("${assets.maximalist}") String maximalist,
                                   @Value("${assets.conservative}") String conservative,
                                   @Value("${assets.gambler}") String gambler,
                                   @Value("${assets.custom}") String custom,
                                   @Value("${messages.asset.emptyPortfolio}") String emptyPortfolio,
                                   @Value("${messages.asset.maximalistInfo}") String maximalistInfo,
                                   @Value("${messages.asset.missingCoinsConserv}") String missingCoinsConserv,
                                   @Value("${messages.asset.missingCoinsGambler}") String missingCoinsGambler) {
        this.userServiceImp = userServiceImp;
        this.repository = repository;
        this.client = client;
        this.maximalist = maximalist;
        this.conservative = conservative;
        this.gambler = gambler;
        this.custom = custom;
        this.emptyPortfolio = emptyPortfolio;
        this.maximalistInfo = maximalistInfo;
        this.missingCoinsConserv = missingCoinsConserv;
        this.missingCoinsGambler = missingCoinsGambler;
    }

    public void saveAsset(String assetsAllocation, Model model, Principal principal) {
        User user = userServiceImp.loadLoggedInUser(principal);
        if (user.getCoinIds().isEmpty()) {
            model.addAttribute("assetMessage", emptyPortfolio);
        } else {
            user.setAssetsAllocation(assetsAllocation);

            boolean tierTwo = userServiceImp.hasAllTier(user);
            boolean tierAll = userServiceImp.hasAllTier(user);
            if (user.getAssetsAllocation().equals(maximalist)) {

                userServiceImp.saveUser(user);
                model.addAttribute("assetMessage", maximalistInfo);
            }
            if (user.getAssetsAllocation().equals(gambler) && !tierAll) {
                model.addAttribute("assetMessage", missingCoinsGambler);
            }
            if (user.getAssetsAllocation().equals(gambler) && tierAll) {
                userServiceImp.saveUser(user);

            }
            if (user.getAssetsAllocation().equals(conservative) && !tierTwo) {
                model.addAttribute("assetMessage", missingCoinsConserv);

            }
            if (user.getAssetsAllocation().equals(conservative) && tierTwo) {
                userServiceImp.saveUser(user);
            }
        }
    }

    public void saveCustomAsset(CustomForm form, Principal principal, Model model) {
        var user = userServiceImp.loadLoggedInUser(principal);
        var coinList = createList(principal, model);
        if (form != null) {
            user.setAssetsAllocation(custom);
            var customAllocation = new CustomAssetAllocation();
            customAllocation.setAllocationName(custom);
            customAllocation.setInvestedCoins(parseCustomDTOsToString(form));
            customAllocation.setUser(user);
            repository.save(customAllocation);
        }
        List<CustomDTO> customDTOs = new ArrayList<>();
        model.addAttribute("tableTitle", "My Coins");
        model.addAttribute("portfolio", user.getCoinIds());
        model.addAttribute("coinList", coinList);
    }

    public Set<String> parseCustomDTOsToString(CustomForm form) {
        return form.getCustomDTOs().stream()
                .map(customDTO -> customDTO.getName() + ";" + customDTO.getPercentage())
                .collect(Collectors.toSet());
    }

    public Map<String, Double> parseStringToMap(Set<String> investedCoins) {
        Map<String, Double> coins = new HashMap<>();
        investedCoins.forEach(elem -> {
            var list = elem.split(";");
            coins.put(list[0], Double.parseDouble(list[1]));
        });
        return coins;
    }

    public List<String> createList(Principal principal, Model model) {
        var user = userServiceImp.loadLoggedInUser(principal);
        var coinList = client.getCoinsById(user.getCoinIds())
                .stream()
                .map(Coin::getName)
                .collect(Collectors.toList());
        model.addAttribute("coinList", coinList);
        return coinList;
    }
}

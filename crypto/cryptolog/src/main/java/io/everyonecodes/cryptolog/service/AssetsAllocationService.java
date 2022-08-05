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
    
    private final UserService userServiceImp;
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
    private final String customError;
    private final String customSuccess;
    
    public AssetsAllocationService(UserService userServiceImp,
                                   CustomAssetAllocationRepository repository,
                                   CoingeckoClient client,
                                   @Value("${assets.maximalist}") String maximalist,
                                   @Value("${assets.conservative}") String conservative,
                                   @Value("${assets.gambler}") String gambler,
                                   @Value("${assets.custom}") String custom,
                                   @Value("${messages.asset.emptyPortfolio}") String emptyPortfolio,
                                   @Value("${messages.asset.maximalistInfo}") String maximalistInfo,
                                   @Value("${messages.asset.missingCoinsConserv}") String missingCoinsConserv,
                                   @Value("${messages.asset.missingCoinsGambler}") String missingCoinsGambler,
                                   @Value("${messages.asset.customError}") String customError,
                                   @Value("${messages.asset.customSuccess}") String customSuccess) {
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
        this.customError = customError;
        this.customSuccess = customSuccess;
    }
    
    public void saveAsset(String assetsAllocation, Model model, Principal principal) {
        User user = userServiceImp.loadLoggedInUser(principal);
        if (user.getCoinIds().isEmpty()) {
            model.addAttribute("assetMessage", emptyPortfolio);
        } else {
            user.setAssetsAllocation(assetsAllocation);
            
            boolean tierTwo = userServiceImp.hasAllTier(user);
            boolean tierThree = userServiceImp.hasTierThree(user);
            boolean tierAll = userServiceImp.hasAllTier(user);
            if (user.getAssetsAllocation().equals(maximalist)) {
                
                userServiceImp.save(user);
                model.addAttribute("assetMessage", maximalistInfo);
            }
            if (user.getAssetsAllocation().equals(gambler) && !tierAll) {
                model.addAttribute("assetMessage", missingCoinsGambler);
            }
            if (user.getAssetsAllocation().equals(gambler) && tierAll) {
                userServiceImp.save(user);
                
            }
            if (user.getAssetsAllocation().equals(conservative) && !tierTwo && !tierThree) {
                model.addAttribute("assetMessage", missingCoinsConserv);
                
            }
            if (user.getAssetsAllocation().equals(conservative) && tierTwo) {
                userServiceImp.save(user);
            }
        }
    }
    
    // Uses coin.getName() instead of id
    public void saveCustomAsset(CustomForm form, Principal principal, Model model) {
        var user = userServiceImp.loadLoggedInUser(principal);
        if (form != null) {
            var percentages = form.getCustomDTOs();
            var sumOfPercentages = percentages.values().stream().reduce(0.0, Double::sum);
            if (sumOfPercentages > 100.0 || sumOfPercentages < 100.0) {
                model.addAttribute("assetMessage", customError);
            } else {
                user.setAssetsAllocation(custom);
                var oExistingCustom = repository.findByCustomAllocationNameAndUser(custom, user);
                if (oExistingCustom.isPresent()) {
                    var existingCustom = oExistingCustom.get();
                    existingCustom.setInvestedCoins(parseCustomDTOsToString(form));
                    repository.save(existingCustom);
                    model.addAttribute("assetMessage", customSuccess);
                } else {
                    var customAllocation = new CustomAssetAllocation();
                    customAllocation.setAllocationName(custom);
                    customAllocation.setInvestedCoins(parseCustomDTOsToString(form));
                    customAllocation.setUser(user);
                    repository.save(customAllocation);
                    model.addAttribute("assetMessage", customSuccess);
                }
            }
        }
        model.addAttribute("tableTitle", "My Portfolio");
    }
    
    public Set<String> parseCustomDTOsToString(CustomForm form) {
        var map = form.getCustomDTOs();
        Set<String> set = new HashSet<>();
        for (var key : map.keySet()) {
            set.add(key + ";" + map.get(key));
        }
        return set;
    }
    
    public Map<String, Double> parseCustomDTOsStringToMap(Set<String> investedCoins) {
        Map<String, Double> coins = new HashMap<>();
        investedCoins.forEach(elem -> {
            var list = elem.split(";");
            coins.put(list[0], Double.parseDouble(list[1]));
        });
        return coins;
    }
    
    public List<String> createList(Principal principal, Model model) {
        var user = userServiceImp.loadLoggedInUser(principal);
        if (user.getCoinIds().isEmpty()) {
            model.addAttribute("assetMessage", emptyPortfolio);
        } else {
            var coinList = client.getCoinsById(user.getCoinIds())
                                 .stream()
                                 .map(Coin::getName)
                                 .collect(Collectors.toList());
            model.addAttribute("coinList", coinList);
            return coinList;
        }
        return List.of();
    }
}

package io.everyonecodes.cryptolog.controller;

import io.everyonecodes.cryptolog.CoingeckoClient;
import io.everyonecodes.cryptolog.data.CustomForm;
import io.everyonecodes.cryptolog.service.AssetsAllocationService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class AssetController {
    
    private final CoingeckoClient client;
    private final AssetsAllocationService assetsAllocationService;
    
    public AssetController(CoingeckoClient client, AssetsAllocationService assetsAllocationService) {
        this.client = client;
        this.assetsAllocationService = assetsAllocationService;
    }
    
    @GetMapping("/asset")
    public String display(@RequestParam(required = false) String assetsAllocation,
                          Model model, Principal principal) {
        var coinList = assetsAllocationService.createList(principal, model);
        CustomForm form = new CustomForm(coinList);
        for (var coin : coinList) {
            form.getCustomDTOs().put(coin, 0d);
        }
        model.addAttribute("coinList", coinList);
        model.addAttribute("form", form);
        if (assetsAllocation == null || assetsAllocation.equals("custom") ) {
            return "asset";
        }
        assetsAllocationService.saveAsset(assetsAllocation, model, principal);
        return "asset";
    }
    
    @PostMapping("/asset")
    String saveCustomAsset(@ModelAttribute("form") CustomForm form,
                           Principal principal,
                           Model model) {
        assetsAllocationService.saveCustomAsset(form, principal, model);
        return "asset";
    }
}
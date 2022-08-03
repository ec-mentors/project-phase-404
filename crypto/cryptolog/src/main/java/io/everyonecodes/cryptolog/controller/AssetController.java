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
import java.util.ArrayList;
import java.util.List;

@Controller
public class AssetController {

    private final CoingeckoClient client;
    private final AssetsAllocationService assetsAllocationService;

    private static List<String> assetsAllocations;

    static {
        assetsAllocations = new ArrayList<>();
        assetsAllocations.add("Maximalist");
        assetsAllocations.add("Conservative");
        assetsAllocations.add("Gambler");
    }

    public AssetController(CoingeckoClient client, AssetsAllocationService assetsAllocationService) {
        this.client = client;
        this.assetsAllocationService = assetsAllocationService;
    }

    @GetMapping("/asset")
    public String display(@RequestParam(required = false) String assetsAllocation,
                          Model model, Principal principal) {
        var coinList = assetsAllocationService.createList(principal, model);
        CustomForm form = new CustomForm(coinList);
        model.addAttribute("coinList", coinList);
        model.addAttribute("form", form);
        if (assetsAllocation == null) {
            return "asset";
        }
        assetsAllocationService.saveAsset(assetsAllocation, model, principal);
        return "asset";
    }

    @PostMapping("/asset/save")
    String saveCustomAsset(@ModelAttribute("form") CustomForm form,
                           Principal principal,
                           Model model) {
        assetsAllocationService.saveCustomAsset(form, principal, model);
        return "asset";
    }
}
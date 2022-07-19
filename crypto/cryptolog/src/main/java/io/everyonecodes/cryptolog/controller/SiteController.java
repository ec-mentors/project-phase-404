package io.everyonecodes.cryptolog.controller;

import io.everyonecodes.cryptolog.CoingeckoClient;
import io.everyonecodes.cryptolog.data.Coin;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Controller
public class SiteController {

    private List<Coin> coinList;
    private final CoingeckoClient client;

    public SiteController(CoingeckoClient client) {
        this.client = client;
    }

    @GetMapping
    String index() {
        return "index";
    }

    @GetMapping("/portfolio")
    String protfolio() {
        return "portfolio";
    }

    @GetMapping("/asset")
    String asset() {
        return "asset";
    }

    @GetMapping("/calculator")
    String calculator() {
        return "calculator";
    }

    @GetMapping("/about")
    String about() {
        return "about";
    }

    @GetMapping("/home")
    public String top100(Model model) {
        try { // how can this be more DRY?
            coinList = client.getTop100ByMarketCap();
        } catch (RestClientException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "clientError";
        }
        model.addAttribute(coinList);
        return "home";
    }

    @PostMapping("/home")
    public String filter(Model model, @RequestParam("filter") String filter) {
        if (coinList == null) {
            try { // how can this be more DRY?
                coinList = client.getTop100ByMarketCap();
            } catch (RestClientException e) {
                model.addAttribute("errorMessage", e.getMessage());
                return "clientError";
            }
        }
        String filterString = filter.toLowerCase();
        var filteredList = coinList.stream().filter(coin ->
                coin.getName().toLowerCase().contains(filterString) ||
                        coin.getSymbol().toLowerCase().contains(filterString)
        ).toList();
        model.addAttribute("coinList", filteredList);
        model.addAttribute("filter", filter);
        return "home";
    }
}

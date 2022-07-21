package io.everyonecodes.cryptolog.controller;

import io.everyonecodes.cryptolog.CoingeckoClient;
import io.everyonecodes.cryptolog.data.Coin;
import io.everyonecodes.cryptolog.data.User;
import io.everyonecodes.cryptolog.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;

import java.security.Principal;
import java.util.List;

@Controller
public class SiteController {

    private List<Coin> coinList;
    private final CoingeckoClient client;

    private final UserService userService;

    public SiteController(CoingeckoClient client, UserService userService) {
        this.client = client;
        this.userService = userService;
    }

    @GetMapping
    String index() {
        return "index";
    }

//    @GetMapping("/portfolio")
//    String protfolio() {
//        return "portfolio";
//    }

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

//    @GetMapping("/home")
//    public String top100(Model model) {
//        try { // how can this be more DRY?
//            coinList = client.getTop100ByMarketCap();
//        } catch (RestClientException e) {
//            model.addAttribute("errorMessage", e.getMessage());
//            return "clientError";
//        }
//        model.addAttribute(coinList);
//        return "home";
//    }

    @GetMapping("/home")
    public String filter(Model model,
                         @RequestParam(required = false) String filter,
                         @RequestParam(required = false) String coinId,
                         Principal principal) {

        User user = userService.loadLoggedInUser(principal);

        if (coinId == null) { // Load top100, if not already loaded or fresh pageload (i.e. other user)
            try { // how can this be more DRY?
                if (filter != null && !filter.isBlank()) {
                    coinList = client.findCoinsFromAll(filter);
                } else {
                    coinList = client.getTop100ByMarketCap();
                }
            } catch (RestClientException e) {
                model.addAttribute("errorMessage", e.getMessage());
                return "clientError";
            }
        } else {
            if (user.getCoinIds().contains(coinId)) { // add / remove coins to portfolio
                user.getCoinIds().remove(coinId);
            } else {
                user.getCoinIds().add(coinId);
            }
            userService.saveUser(user);
        }
        model.addAttribute(coinList);
        model.addAttribute("tableTitle", "Top 100 Coins");
        model.addAttribute("filter", filter);
        model.addAttribute("portfolio", user.getCoinIds());
        model.addAttribute("target", "/home");
        return "home";
    }

    @GetMapping("/portfolio")
    public String portfolio(Model model,
                         @RequestParam(required = false) String filter,
                         @RequestParam(required = false) String coinId,
                         Principal principal) {

        User user = userService.loadLoggedInUser(principal);

        if (coinId != null) { // remove coins from portfolio
            user.getCoinIds().remove(coinId);
            userService.saveUser(user);
        }
        if (user.getCoinIds().isEmpty()) {
            return "portfolio-empty";
        }
        List<Coin> portfolioList; // Always load portfolio from API
        try { // how can this be more DRY?
            portfolioList = client.getCoinsById(user.getCoinIds());
        } catch (RestClientException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "clientError";
        }
        if (filter != null && !filter.isBlank()) {  // filter coin list
            var searchList = client.findCoinsFromAll(filter);
            model.addAttribute(searchList);
        } else {
            model.addAttribute(portfolioList);
        }
        model.addAttribute("tableTitle", "My Portfolio");
        model.addAttribute("filter", filter);
        model.addAttribute("portfolio", user.getCoinIds());
        model.addAttribute("target", "/portfolio");
        return "home";
    }

    private List<Coin> getFilteredList(List<Coin> coinList, String filter) {
        return coinList.stream().filter(
                coin -> coin.getName().toLowerCase().contains(filter) ||
                        coin.getSymbol().toLowerCase().contains(filter)
        ).toList();
    }
}

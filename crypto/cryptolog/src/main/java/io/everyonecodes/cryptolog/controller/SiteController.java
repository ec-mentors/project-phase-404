package io.everyonecodes.cryptolog.controller;

import io.everyonecodes.cryptolog.CoingeckoClient;
import io.everyonecodes.cryptolog.data.Coin;
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
    private List<Coin> portfolioCoinList;
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

        String userName = principal.getName();
        var user = userService.findUserByEmail(userName).get();

        // Load top100, if not already loaded
        if (coinList == null) {
            try { // how can this be more DRY?
                coinList = client.getTop100ByMarketCap();
            } catch (RestClientException e) {
                model.addAttribute("errorMessage", e.getMessage());
                return "clientError";
            }
        }
        // add / remove coins to portfolie
        if (coinId != null) {
            if (user.getCoinIds().contains(coinId)) {
                user.getCoinIds().remove(coinId);
            } else {
                user.getCoinIds().add(coinId);
            }
            userService.saveUser(user);
        }
        // filter coin list
        if (filter != null) {
            String filterString = filter.toLowerCase();
            var filteredList = coinList.stream().filter(
                    coin -> coin.getName().toLowerCase().contains(filterString) ||
                            coin.getSymbol().toLowerCase().contains(filterString)
            ).toList();
            model.addAttribute("coinList", filteredList);
        } else {
            model.addAttribute(coinList);
        }
        model.addAttribute("filter", filter);
        model.addAttribute("portfolio", user.getCoinIds());
        return "home";
    }

    @GetMapping("/portfolio")
    public String portfolio(Model model,
                         @RequestParam(required = false) String filter,
                         @RequestParam(required = false) String coinId,
                         Principal principal) {

        String userName = principal.getName();
        var user = userService.findUserByEmail(userName).get();

        // Load top100, if not already loaded
        if (portfolioCoinList == null) {
            try { // how can this be more DRY?
                portfolioCoinList = client.getCoinsById(user.getCoinIds());
            } catch (RestClientException e) {
                model.addAttribute("errorMessage", e.getMessage());
                return "clientError";
            }
        }
        // add / remove coins to portfolie
        if (coinId != null) {
            if (user.getCoinIds().contains(coinId)) {
                user.getCoinIds().remove(coinId);
            } else {
                user.getCoinIds().add(coinId);
            }
            userService.saveUser(user);
        }
        // filter coin list
        if (filter != null) {
            String filterString = filter.toLowerCase();
            var filteredList = portfolioCoinList.stream().filter(
                    coin -> coin.getName().toLowerCase().contains(filterString) ||
                            coin.getSymbol().toLowerCase().contains(filterString)
            ).toList();
            model.addAttribute("coinList", filteredList);
        } else {
            model.addAttribute(portfolioCoinList);
        }
        model.addAttribute("filter", filter);
        model.addAttribute("portfolio", user.getCoinIds());
        return "home";
    }
}

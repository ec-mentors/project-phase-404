package io.everyonecodes.cryptolog.controller;

import io.everyonecodes.cryptolog.CoingeckoClient;
import io.everyonecodes.cryptolog.data.Coin;
import io.everyonecodes.cryptolog.data.User;
import io.everyonecodes.cryptolog.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

        if (coinId == null) {
            if (filter != null && !filter.isBlank()) {
                coinList = client.findCoinsFromAll(filter);
            } else {
                coinList = client.getTop100ByMarketCap();
            }
        } else {
            if (user.getCoinIds().contains(coinId)) { // add / remove coins to portfolio
                user.getCoinIds().remove(coinId);
            } else {
                user.getCoinIds().add(coinId);
            }
            userService.saveUser(user);
        }

        List<Coin> coinFiveList; // Generate String for "Top 5 Discount" scrolling text - pretty sure this can be more clean/DRY!
        if (filter != null && !filter.isBlank()) {
            coinFiveList = client.getTop100ByMarketCap().stream()
                    .sorted(Comparator.comparing(Coin::getAth_change_percentage))
                    .limit(5).toList();
        } else {
            coinFiveList = coinList.stream()
                    .sorted(Comparator.comparing(Coin::getAth_change_percentage))
                    .limit(5).toList();
        }
        String coinString = coinFiveList.stream()
                .map(data -> data.getName().toUpperCase() + ": " + data.getCurrent_price()+ " USD")
                .collect(Collectors.joining( " +++ ", "TOP FIVE COINS IN DISCOUNT: +++ ", " +++"));

        model.addAttribute("coinString", coinString);
        model.addAttribute(coinList);
        model.addAttribute("tableTitle", "Top 100 Coins");
        model.addAttribute("filter", filter);
        model.addAttribute("target", "/home");
        model.addAttribute("portfolio", user.getCoinIds());
        return "home";
    }

    @GetMapping("/portfolio")
    public String portfolio(Model model,
                            @RequestParam(required = false) String filter,
                            @RequestParam(required = false) String coinId,
                            Principal principal) {

        User user = userService.loadLoggedInUser(principal);

        if (coinId != null) { // remove coins from portfolio
            if (user.getCoinIds().contains(coinId)) { // add / remove coins to portfolio
                user.getCoinIds().remove(coinId);
            } else {
                user.getCoinIds().add(coinId);
            }
            userService.saveUser(user);
        }
        if (user.getCoinIds().isEmpty()) {
            return "portfolio-empty";
        }
        if (filter != null && !filter.isBlank()) {  // search all coins
            if (coinId == null) {
                coinList = client.findCoinsFromAll(filter);
            }
        } else {
            coinList = client.getCoinsById(user.getCoinIds());
        }
        model.addAttribute(coinList);
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

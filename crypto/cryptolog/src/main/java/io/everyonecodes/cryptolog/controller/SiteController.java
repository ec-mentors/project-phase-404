package io.everyonecodes.cryptolog.controller;

import io.everyonecodes.cryptolog.CoingeckoClient;
import io.everyonecodes.cryptolog.data.Coin;
import io.everyonecodes.cryptolog.data.User;
import io.everyonecodes.cryptolog.service.UserService;
import io.everyonecodes.cryptolog.service.UserServiceImp;
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
    private final UserServiceImp userServiceImp;

    public SiteController(CoingeckoClient client, UserService userService, UserServiceImp userServiceImp) {
        this.client = client;
        this.userService = userService;
        this.userServiceImp = userServiceImp;
    }

    @GetMapping
    String index() {
        return "index";
    }


    @GetMapping("/calculator")
    String calculator() {
        return "calculator";
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

        List<Coin> displayList;
        if (filter != null && !filter.isBlank()) {
            displayList = client.findCoinsFromAll(filter); // do not store in coinList
        } else {
            if (coinId == null) {
                coinList = client.getTop100ByMarketCap();
            }
            displayList = coinList;
        }

        if (coinId != null) {
            if (user.getCoinIds().contains(coinId)) { // add / remove coins to portfolio
                user.getCoinIds().remove(coinId);
            } else {
                user.getCoinIds().add(coinId);
            }
            userService.saveUser(user);
        }

        String coinString = coinList.stream()
                .sorted(Comparator.comparing(Coin::getAth_change_percentage))
                .limit(5)
                .map(data -> data.getName().toUpperCase() + ": " + data.getCurrent_price() + " USD")
                .collect(Collectors.joining(" +++ ", "COINS AT DISCOUNT: +++ ", " +++"));

        model.addAttribute("coinString", coinString);
        model.addAttribute(displayList);
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
        List<Coin> displayList;
        if (filter != null && !filter.isBlank()) {  // search all coins
            displayList = client.findCoinsFromAll(filter);
        } else {
            displayList = client.getCoinsById(user.getCoinIds());
        }
        String coinString = coinList.stream()
                .sorted(Comparator.comparing(Coin::getPrice_change_percentage_24h).reversed())
                .limit(3)
                .map(coin -> coin.getName().toUpperCase() + ": " + coin.getCurrent_price() + " USD "
                        + (coin.getPrice_change_percentage_24h() >= 0d ? "+" : "") + coin.getPrice_change_percentage_24h() + "%")
                .collect(Collectors.joining(" +++ ", "COINS OF THE DAY: +++ ", " +++"));

        model.addAttribute("coinString", coinString);
        model.addAttribute(displayList);
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

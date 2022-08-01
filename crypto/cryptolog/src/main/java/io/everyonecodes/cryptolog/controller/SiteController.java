package io.everyonecodes.cryptolog.controller;

import io.everyonecodes.cryptolog.CoingeckoClient;
import io.everyonecodes.cryptolog.data.Coin;
import io.everyonecodes.cryptolog.data.User;
import io.everyonecodes.cryptolog.service.UserService;
import io.everyonecodes.cryptolog.service.UserServiceImp;
import io.everyonecodes.cryptolog.service.YieldCalculatorService;
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

    private List<Coin> top100CoinList;
    private final CoingeckoClient client;
    private final UserService userService;
    private final UserServiceImp userServiceImp;
    private final YieldCalculatorService yieldCalculatorService;


    public SiteController(CoingeckoClient client, UserService userService, UserServiceImp userServiceImp, YieldCalculatorService yieldCalculatorService) {
        this.client = client;
        this.userService = userService;
        this.userServiceImp = userServiceImp;

        this.yieldCalculatorService = yieldCalculatorService;
    }

    @GetMapping
    String index() {
        return "index";
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
                         @RequestParam(required = false) String sorting,
                         Principal principal) {

        User user = userService.loadLoggedInUser(principal);

        List<Coin> displayList;
        if (filter != null && !filter.isBlank()) {
            displayList = client.findCoinsFromAll(filter); // do not store in coinList
        } else {
            if (coinId == null && sorting == null) {
                top100CoinList = List.copyOf(client.getTop100ByMarketCap());
            }
            displayList = top100CoinList;
        }

        if (coinId != null) {
            if (user.getCoinIds().contains(coinId)) { // add / remove coins to portfolio
                user.getCoinIds().remove(coinId);
            } else {
                user.getCoinIds().add(coinId);
            }
            userService.saveUser(user);
        }

        if (sorting != null) {
            switch (sorting) {
                case "rank_asc" -> displayList = displayList.stream().sorted(Comparator.comparing(Coin::getMarket_cap_rank)).toList();
                case "rank_desc" -> displayList = displayList.stream().sorted(Comparator.comparing(Coin::getMarket_cap_rank).reversed()).toList();
                case "name_asc" -> displayList = displayList.stream().sorted(Comparator.comparing(coin -> coin.getName().toLowerCase())).toList();
                case "name_desc" -> displayList = displayList.stream().sorted(Comparator.comparing((Coin coin) -> coin.getName().toLowerCase()).reversed()).toList();
                case "price_asc" -> displayList = displayList.stream().sorted(Comparator.comparing(Coin::getCurrent_price)).toList();
                case "price_desc" -> displayList = displayList.stream().sorted(Comparator.comparing(Coin::getCurrent_price).reversed()).toList();
                case "ath_asc" -> displayList = displayList.stream().sorted(Comparator.comparing(Coin::getAth)).toList();
                case "ath_desc" -> displayList = displayList.stream().sorted(Comparator.comparing(Coin::getAth).reversed()).toList();
                case "ath_drop_asc" -> displayList = displayList.stream().sorted(Comparator.comparing(Coin::getAth_change_percentage)).toList();
                case "ath_drop_desc" -> displayList = displayList.stream().sorted(Comparator.comparing(Coin::getAth_change_percentage).reversed()).toList();
                case "portfolio_asc" -> displayList = displayList.stream().sorted(Comparator.comparing(coin -> !user.getCoinIds().contains(coin.getId()))).toList();
                case "portfolio_desc" -> displayList = displayList.stream().sorted(Comparator.comparing(coin -> user.getCoinIds().contains(coin.getId()))).toList();
            }
        }

        String coinString = top100CoinList.stream()
                .sorted(Comparator.comparing(Coin::getAth_change_percentage))
                .limit(5)
                .map(coin -> coin.getName().toUpperCase() + ": " + coin.getCurrent_price() + " USD – ATH Drop: "
                        + String.format("%+.2f", coin.getAth_change_percentage()) + "%")
                .collect(Collectors.joining(" +++ ", "COINS AT DISCOUNT: +++ ", " +++"));

        model.addAttribute("sorting", sorting);
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
        String coinString = top100CoinList.stream()
                .sorted(Comparator.comparing(Coin::getPrice_change_percentage_24h).reversed())
                .limit(3)
                .map(coin -> coin.getName().toUpperCase() + ": " + coin.getCurrent_price() + " USD – 24h Change: "
                        + String.format("%+.2f", coin.getPrice_change_percentage_24h()) + "%")
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

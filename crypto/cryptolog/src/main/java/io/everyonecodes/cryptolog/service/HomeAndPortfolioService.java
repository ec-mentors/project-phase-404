package io.everyonecodes.cryptolog.service;

import io.everyonecodes.cryptolog.CoingeckoClient;
import io.everyonecodes.cryptolog.data.Coin;
import io.everyonecodes.cryptolog.data.User;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HomeAndPortfolioService {

    private List<Coin> top100CoinList;
    private final UserService userService;
    private final CoingeckoClient client;

    public HomeAndPortfolioService(UserService userService, CoingeckoClient client) {
        this.userService = userService;
        this.client = client;
    }

    public String getHome(String filter, String coinId, String sorting, Principal principal, Model model) {

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
            displayList = sortDisplayList(displayList, sorting, user);
        }

        model.addAttribute("sorting", sorting);
        model.addAttribute("coinString", getCoinsAtDiscount());
        model.addAttribute(displayList);
        model.addAttribute("tableTitle", "Top 100 Coins");
        model.addAttribute("filter", filter);
        model.addAttribute("target", "/home");
        model.addAttribute("portfolio", user.getCoinIds());

        return "home";
    }

    public String getPortfolio(String filter, String coinId, Principal principal, Model model) {

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

        model.addAttribute("coinString", getCoinsOfThDay());
        model.addAttribute(displayList);
        model.addAttribute("tableTitle", "My Portfolio");
        model.addAttribute("filter", filter);
        model.addAttribute("portfolio", user.getCoinIds());
        model.addAttribute("target", "/portfolio");

        return "home";
    }

    private String getCoinsAtDiscount() {
        return top100CoinList.stream()
                .sorted(Comparator.comparing(Coin::getAth_change_percentage))
                .limit(5)
                .map(coin -> coin.getName().toUpperCase() + ": " + coin.getCurrent_price() + " USD – ATH Drop: "
                        + String.format("%+.2f", coin.getAth_change_percentage()) + "%")
                .collect(Collectors.joining(" +++ ", "COINS AT DISCOUNT: +++ ", " +++"));
    }

    private String getCoinsOfThDay() {
        return top100CoinList.stream()
                .sorted(Comparator.comparing(Coin::getPrice_change_percentage_24h).reversed())
                .limit(3)
                .map(coin -> coin.getName().toUpperCase() + ": " + coin.getCurrent_price() + " USD – 24h Change: "
                        + String.format("%+.2f", coin.getPrice_change_percentage_24h()) + "%")
                .collect(Collectors.joining(" +++ ", "COINS OF THE DAY: +++ ", " +++"));
    }

    private List<Coin> sortDisplayList(List<Coin> displayList, String sorting, User user) {
        switch (sorting) {
            case "rank_asc" -> displayList = displayList.stream().sorted(Comparator.comparing(coin -> coin.getMarket_cap_rank() > 0 ? coin.getMarket_cap_rank() : Integer.MAX_VALUE)).toList();
            case "rank_desc" -> displayList = displayList.stream().sorted(Comparator.comparing((Coin coin) -> coin.getMarket_cap_rank() > 0 ? coin.getMarket_cap_rank() : Integer.MAX_VALUE).reversed()).toList();
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
        return displayList;
    }

    private List<Coin> filterList(List<Coin> coinList, String filter) {
        return coinList.stream().filter(
                coin -> coin.getName().toLowerCase().contains(filter) ||
                        coin.getSymbol().toLowerCase().contains(filter)
        ).toList();
    }
}

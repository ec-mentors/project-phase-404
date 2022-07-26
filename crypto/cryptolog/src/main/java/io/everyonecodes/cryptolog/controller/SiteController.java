package io.everyonecodes.cryptolog.controller;

import io.everyonecodes.cryptolog.CoingeckoClient;
import io.everyonecodes.cryptolog.data.Coin;
import io.everyonecodes.cryptolog.data.User;
import io.everyonecodes.cryptolog.data.YieldData;
import io.everyonecodes.cryptolog.service.UserService;
import io.everyonecodes.cryptolog.service.YieldCalculatorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class SiteController {

    private List<Coin> coinList;
    private final CoingeckoClient client;
    private final YieldCalculatorService yieldCalculatorService;

    private final UserService userService;

    public SiteController(CoingeckoClient client, YieldCalculatorService yieldCalculatorService, UserService userService) {
        this.client = client;
        this.yieldCalculatorService = yieldCalculatorService;
        this.userService = userService;
    }

    @GetMapping
    String index() {
        return "index";
    }
    // 1000 ---- 20000  180%
    //20000 ---- 60000 44%
    //60000 ----

    @GetMapping("/asset")
    String asset() {
        return "asset";
    }

    @GetMapping("/calculator")
    public String getYieldResults(Model model,
                                  @RequestParam(required = false) String monthlyAmount,
                                  @RequestParam(required = false) String period,
                                  Principal principal) {

        List<YieldData> yieldDataList = new ArrayList<>();
        User user = userService.loadLoggedInUser(principal);
        List<Coin> coinList = client.getCoinsById(user.getCoinIds());
        if (monthlyAmount == null) {
            monthlyAmount = "0";
        }
        if (monthlyAmount.isBlank()) {
            monthlyAmount = "0";
        }
        if (period == null) {
            period = "0";
        }
        if (period.isBlank()) {
            period = "0";
        }
        if (period.equals("0")) {
            model.addAttribute("finalProfit", monthlyAmount);
            model.addAttribute("finalInvestment", monthlyAmount);
        } else {
            for (Coin coin : coinList) {
                yieldDataList.add(yieldCalculatorService.calculate(coin.getId(),
                        BigDecimal.valueOf(Long.parseLong((monthlyAmount))),
                        BigDecimal.valueOf(coin.getCurrent_price()),
                        BigDecimal.valueOf(coin.getAth()),
                        Integer.parseInt(period)));

            }

            BigDecimal finalProfit = new BigDecimal(0);
            for (YieldData yieldData : yieldDataList) {
                finalProfit = finalProfit.add(yieldData.getProfit());
            }
            BigDecimal finalInvestmentAmount = new BigDecimal(0);
            for (YieldData yieldData : yieldDataList) {
                finalInvestmentAmount = finalInvestmentAmount.add(yieldData.getInvestedAmount());
            }
            model.addAttribute("finalProfit", finalProfit.setScale(2, RoundingMode.HALF_UP));
            model.addAttribute("finalInvestment", finalInvestmentAmount.setScale(2, RoundingMode.HALF_UP));
            model.addAttribute(yieldDataList);
            model.addAttribute("monthlyAmount", monthlyAmount);
            model.addAttribute("period", period);
            model.addAttribute("tableTitle", "CryptoLog Forecast");
            model.addAttribute("coin");
            model.addAttribute("investedAmount");
            model.addAttribute("forecastedValue");
            model.addAttribute("profit");
            model.addAttribute("accumulated");
        }

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
        List<Coin> coinFiveList = client.getTop100ByMarketCap().stream().sorted(Comparator.comparing(Coin::getAth_change_percentage))
                .limit(5).toList();

        String coinString = coinFiveList.stream()
                .map(data -> data.getName().toUpperCase() + ": " + data.getCurrent_price() + " USD" + " +++ ")
                .collect(Collectors.joining(" ", "TOP FIVE COINS IN DISCOUNT:  +++  ", ""));
        coinString = coinString.substring(0, coinString.length() - 4);
        model.addAttribute("coinString", coinString);

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

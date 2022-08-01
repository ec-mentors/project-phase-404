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
public class PorfolioBalancingController {
    private final CoingeckoClient client;
    private final UserService userService;
    public PorfolioBalancingController(CoingeckoClient client, UserService userService) {
        this.client = client;
        this.userService = userService;
    }

    @GetMapping("/portfoliobalance")
    public String portfoliobalance(Model model,
                            @RequestParam(required = false) String filter,
                            @RequestParam(required = false) String coinId,
                            Principal principal) {

        User user = userService.loadLoggedInUser(principal);
        List<Coin> coinList = client.getCoinsById(user.getCoinIds());

        model.addAttribute(coinList);
        model.addAttribute("tableTitle", "Portfolio Balancing");
        model.addAttribute("filter", filter);
        model.addAttribute("target", "/portfoliobalance");
        return "portfoliobalance";
    }
}

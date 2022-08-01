package io.everyonecodes.cryptolog.controller;

import io.everyonecodes.cryptolog.CoingeckoClient;
import io.everyonecodes.cryptolog.data.BalanceForm;
import io.everyonecodes.cryptolog.data.Coin;
import io.everyonecodes.cryptolog.data.User;
import io.everyonecodes.cryptolog.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.List;
@Controller
public class PorfolioBalancingController {
    private final CoingeckoClient client;
    private final UserService userService;
    public PorfolioBalancingController(CoingeckoClient client, UserService userService) {
        this.client = client;
        this.userService = userService;
    }

    @GetMapping("/portfoliobalance")
    public String portfoliobalance(Model model, Principal principal) {

        User user = userService.loadLoggedInUser(principal);
        List<Coin> coinList = client.getCoinsById(user.getCoinIds());

        BalanceForm balanceForm = new BalanceForm();

        model.addAttribute(balanceForm);
        model.addAttribute(coinList);
        return "portfoliobalance";
    }

    @PostMapping("/portfoliobalance")
    public String portfoliorebalance(@ModelAttribute("form") BalanceForm balanceForm, Model model, Principal principal) {
        System.out.println(balanceForm);
        return "portfoliobalance";
    }
}

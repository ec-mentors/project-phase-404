package io.everyonecodes.cryptolog.controller;

import io.everyonecodes.cryptolog.CoingeckoClient;
import io.everyonecodes.cryptolog.data.BalanceForm;
import io.everyonecodes.cryptolog.data.Coin;
import io.everyonecodes.cryptolog.data.PortfolioBalance;
import io.everyonecodes.cryptolog.data.User;
import io.everyonecodes.cryptolog.service.PortfolioBalanceService;
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
    private final PortfolioBalanceService portfolioBalanceService;
    public PorfolioBalancingController(CoingeckoClient client, UserService userService, PortfolioBalanceService portfolioBalanceService) {
        this.client = client;
        this.userService = userService;
        this.portfolioBalanceService = portfolioBalanceService;
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
    public String portfoliobalance(@ModelAttribute BalanceForm balanceForm, Model model, Principal principal) {
        User user = userService.loadLoggedInUser(principal);
        List<Coin> coinList = client.getCoinsById(user.getCoinIds());
        var values = balanceForm.getValues();
        List<PortfolioBalance> results = portfolioBalanceService.getBalance(coinList,balanceForm.getProfile(),values);
        for (PortfolioBalance portfolioBalance:results){
            System.out.println(portfolioBalance.getCoinId()+portfolioBalance.getBalance());
        }
        model.addAttribute(results);
        return "portfoliobalance";
    }
}

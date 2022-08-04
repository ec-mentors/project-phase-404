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
public class PortfolioBalancingController {
    private final CoingeckoClient client;
    private final UserService userService;
    private final PortfolioBalanceService portfolioBalanceService;
    public PortfolioBalancingController(CoingeckoClient client, UserService userService, PortfolioBalanceService portfolioBalanceService) {
        this.client = client;
        this.userService = userService;
        this.portfolioBalanceService = portfolioBalanceService;
    }

    @GetMapping("/portfoliobalance")
    public String portfoliobalance(Model model, Principal principal) {

        User user = userService.loadLoggedInUser(principal);
        List<Coin> coinList = client.getCoinsById(user.getCoinIds());

        BalanceForm balanceForm = new BalanceForm();
        for (var coin : coinList) {
            balanceForm.getValues().put(coin.getId(), 0d);
        }
        model.addAttribute(balanceForm);
        model.addAttribute(coinList);

        return "portfoliobalance";
    }

    @PostMapping("/portfoliobalance")
    public String portfoliobalance(@ModelAttribute BalanceForm balanceForm, Model model, Principal principal) {
        User user = userService.loadLoggedInUser(principal);
        List<Coin> coinList = client.getCoinsById(user.getCoinIds());
        var values = balanceForm.getValues();
        List<PortfolioBalance> balanceResults = portfolioBalanceService.getBalance(coinList, balanceForm.getProfile(), values);
        model.addAttribute("balanceResults", balanceResults);
        model.addAttribute("caption", balanceForm.getProfile());
        return "portfoliobalance";
    }
}

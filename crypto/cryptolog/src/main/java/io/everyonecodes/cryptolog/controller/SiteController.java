package io.everyonecodes.cryptolog.controller;

import io.everyonecodes.cryptolog.service.HomeAndPortfolioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class SiteController {

    private final HomeAndPortfolioService homeAndPortfolioService;


    public SiteController(HomeAndPortfolioService homeAndPortfolioService) {
        this.homeAndPortfolioService = homeAndPortfolioService;
    }

    @GetMapping
    public String index() {
        return "index";
    }

    @GetMapping("/home")
    public String home(@RequestParam(required = false) String filter,
                       @RequestParam(required = false) String coinId,
                       @RequestParam(required = false) String sorting,
                       Principal principal, Model model) {

        return homeAndPortfolioService.getHome(filter, coinId, sorting, principal, model);
    }

    @GetMapping("/portfolio")
    public String portfolio(@RequestParam(required = false) String filter,
                            @RequestParam(required = false) String coinId,
                            Principal principal, Model model) {

        return homeAndPortfolioService.getPortfolio(filter, coinId, principal, model);
    }
}

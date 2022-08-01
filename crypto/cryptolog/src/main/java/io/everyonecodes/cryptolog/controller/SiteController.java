package io.everyonecodes.cryptolog.controller;

import io.everyonecodes.cryptolog.data.Coin;
import io.everyonecodes.cryptolog.data.User;
import io.everyonecodes.cryptolog.service.SiteService;
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
    
    private final SiteService siteService;
    
    public SiteController(SiteService siteService) {
        this.siteService = siteService;
    }
    
    @GetMapping
    String index() {
        return siteService.getIndex();
    }


//    @GetMapping("/home")
//    public String top100(Model model) {
//       return siteService.getTop100(model);
//    }

    @GetMapping("/home")
    public String filter(Model model,
                         @RequestParam(required = false) String filter,
                         @RequestParam(required = false) String coinId,
                         Principal principal) {

        return siteService.getFilter(model, filter, coinId, principal);
    }

    @GetMapping("/portfolio")
    public String portfolio(Model model,
                            @RequestParam(required = false) String filter,
                            @RequestParam(required = false) String coinId,
                            Principal principal) {

        return siteService.getPortfolio(model, filter, coinId, principal);
    }
    
}

package io.everyonecodes.registration;

import io.everyonecodes.registration.data.Coin;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Controller
public class CoinController {

    private final CoingeckoClient client;

    public CoinController(CoingeckoClient client) {
        this.client = client;
    }

    @GetMapping("/home/top100")
    public String top100(Model model) {
        try {
            List<Coin> coinList = client.getTop100ByMarketCap();
            model.addAttribute(coinList);
            return "top100";
        } catch (RestClientException e) {
            model.addAttribute("errorMsg", e.getMessage());
            return "error";
        }
    }
}
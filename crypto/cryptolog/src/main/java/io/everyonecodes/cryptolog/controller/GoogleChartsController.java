package io.everyonecodes.cryptolog.controller;

import io.everyonecodes.cryptolog.CoingeckoClient;
import io.everyonecodes.cryptolog.data.Coin;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Controller
public class GoogleChartsController {

    private final CoingeckoClient coingeckoClient;
    private List<Coin> coinList;

    public GoogleChartsController(CoingeckoClient coingeckoClient) {
        this.coingeckoClient = coingeckoClient;
    }

    @GetMapping("/chart")
    public String getPieChart(Model model) {
        List<Coin> coins = coingeckoClient.getTop100ByMarketCap().stream().limit(10).collect(Collectors.toList());
        Map<String, Double> graphData = new TreeMap<>();
      for(Coin coin : coins) {

          graphData.put(coin.getName(), coin.getAth_change_percentage());
      }


        model.addAttribute("chartData", graphData);
        return "chart";
    }
}
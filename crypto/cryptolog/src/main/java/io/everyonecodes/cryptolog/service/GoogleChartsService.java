package io.everyonecodes.cryptolog.service;

import io.everyonecodes.cryptolog.CoingeckoClient;
import io.everyonecodes.cryptolog.data.Coin;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class GoogleChartsService {
	
	private final CoingeckoClient coingeckoClient;
	private List<Coin> coinList;
	
	public GoogleChartsService(CoingeckoClient coingeckoClient) {
		this.coingeckoClient = coingeckoClient;
	}
	
	public String getChart(Model model) {
		List<Coin> coins = coingeckoClient.getTop100ByMarketCap().stream().limit(10).toList();
		Map<String, Double> graphData = new TreeMap<>();
		for(Coin coin : coins) {
			
			graphData.put(coin.getName(), coin.getAth_change_percentage());
		}
		
		
		model.addAttribute("chartData", graphData);
		return "chart";
	}
}

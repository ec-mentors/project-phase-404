package io.everyonecodes.cryptolog.service;

import io.everyonecodes.cryptolog.CoingeckoClient;
import io.everyonecodes.cryptolog.data.Coin;
import io.everyonecodes.cryptolog.data.User;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.client.RestClientException;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SiteService {
	
	private List<Coin> coinList;
	private final CoingeckoClient client;
	private final UserService userService;
	private final UserServiceImp userServiceImp;
	private final YieldCalculatorService yieldCalculatorService;
	
	
	public SiteService(CoingeckoClient client, UserService userService, UserServiceImp userServiceImp, YieldCalculatorService yieldCalculatorService) {
		this.client = client;
		this.userService = userService;
		this.userServiceImp = userServiceImp;
		
		this.yieldCalculatorService = yieldCalculatorService;
	}
	
	public String getIndex() {
		return "index";
	}
	
//    public String getTop100(Model model) {
//        try { // how can this be more DRY?
//            coinList = client.getTop100ByMarketCap();
//        } catch (RestClientException e) {
//            model.addAttribute("errorMessage", e.getMessage());
//            return "clientError";
//        }
//        model.addAttribute(coinList);
//        return "home";
//    }
	
	public String getFilter(Model model, String filter, String coinId, Principal principal) {
		
		User user = userService.loadLoggedInUser(principal);
		
		List<Coin> displayList;
		if (filter != null && !filter.isBlank()) {
			displayList = client.findCoinsFromAll(filter); // do not store in coinList
		} else {
			if (coinId == null) {
				coinList = client.getTop100ByMarketCap();
			}
			displayList = coinList;
		}
		
		if (coinId != null) {
			if (user.getCoinIds().contains(coinId)) { // add / remove coins to portfolio
				user.getCoinIds().remove(coinId);
			} else {
				user.getCoinIds().add(coinId);
			}
			userService.saveUser(user);
		}
		
		String coinString = coinList.stream()
		                            .sorted(Comparator.comparing(Coin::getAth_change_percentage))
		                            .limit(5)
		                            .map(coin -> coin.getName().toUpperCase() + ": " + coin.getCurrent_price() + " USD – ATH Drop: "
				                            + String.format("%+.2f", coin.getAth_change_percentage()) + "%")
		                            .collect(Collectors.joining(" +++ ", "COINS AT DISCOUNT: +++ ", " +++"));
		
		model.addAttribute("coinString", coinString);
		model.addAttribute(displayList);
		model.addAttribute("tableTitle", "Top 100 Coins");
		model.addAttribute("filter", filter);
		model.addAttribute("target", "/home");
		model.addAttribute("portfolio", user.getCoinIds());
		return "home";
	}
	
	public String getPortfolio(Model model, String filter, String coinId, Principal principal) {
		
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
		List<Coin> displayList;
		if (filter != null && !filter.isBlank()) {  // search all coins
			displayList = client.findCoinsFromAll(filter);
		} else {
			displayList = client.getCoinsById(user.getCoinIds());
		}
		String coinString = coinList.stream()
		                            .sorted(Comparator.comparing(Coin::getPrice_change_percentage_24h).reversed())
		                            .limit(3)
		                            .map(coin -> coin.getName().toUpperCase() + ": " + coin.getCurrent_price() + " USD – 24h Change: "
				                            + String.format("%+.2f", coin.getPrice_change_percentage_24h()) + "%")
		                            .collect(Collectors.joining(" +++ ", "COINS OF THE DAY: +++ ", " +++"));
		
		model.addAttribute("coinString", coinString);
		model.addAttribute(displayList);
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

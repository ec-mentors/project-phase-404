package io.everyonecodes.cryptolog.service;

import io.everyonecodes.cryptolog.data.Coin;
import io.everyonecodes.cryptolog.data.PortfolioBalance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PortfolioBalanceService {
    private final YieldCalculatorService yieldCalculatorService;
    
    private final String conservative;
    private final String gambler;
    

    public PortfolioBalanceService(YieldCalculatorService yieldCalculatorService,
                                   @Value("${assets.conservative}") String conservative,
                                   @Value("${assets.gambler}") String gambler) {
        this.yieldCalculatorService = yieldCalculatorService;
        this.conservative = conservative;
        this.gambler = gambler;
    }

    public List<PortfolioBalance> getBalance(List<Coin> coinList, String profile, Map<String, Double> values, Model model) {
        int tierOne = 0;
        int tierTwo = 0;
        int tierThree = 0;
        double percentage = 0;
        double percentageBefore = 0;
        boolean hasRankOneCoin = false;
        List<PortfolioBalance> balanceResults = new ArrayList<>();
        double accumulatedCoin = 0;
        double valueOfAllCoins = 0;
        for (Coin coin : coinList) {
            if (coin.getMarket_cap_rank() == 1) {
                hasRankOneCoin = true;
            }
            if (coin.getMarket_cap_rank() <= 2) {
                tierOne++;
            } else if ((coin.getMarket_cap_rank() > 2 && coin.getMarket_cap_rank() <= 49)) {
                tierTwo++;
            } else {
                tierThree++;
            }
        }
        for (Coin coin : coinList) {
            valueOfAllCoins += getAccumulatedPrice(coin, values);
        }

        for (Coin coin : coinList) {
            String result = "";
            double initialAccumulatedCoin = values.get(coin.getId());
            double investedAmount = valueOfAllCoins;
            double accumulatedPrice = getAccumulatedPrice(coin, values);

            percentageBefore = accumulatedPrice / valueOfAllCoins * 100;

            if (profile.equals(conservative)) {
                if (tierOne == 0 || tierTwo == 0 || tierThree == 0) {
                    model.addAttribute("errorMessage", "You don't have all three types of coin tiers in your portfolio!");
                    break;

                } else if (coin.getMarket_cap_rank() <= 2) {
                    investedAmount = investedAmount * 0.8 / tierOne;
                    percentage = 80d / tierOne;
                } else if (coin.getMarket_cap_rank() > 2 && coin.getMarket_cap_rank() <= 49) {
                    investedAmount = investedAmount * 0.1 / tierTwo;
                    percentage = 10d / tierTwo;
                } else {
                    investedAmount = investedAmount * 0.1 / tierThree;
                    percentage = 10d / tierThree;
                }

            } else if (profile.equals(gambler)) {
                if (tierOne == 0 || tierTwo == 0 || tierThree == 0) {
                    model.addAttribute("errorMessage", "You don't have all three types of coin tiers in your portfolio!");
                    break;
                } else if (coin.getMarket_cap_rank() <= 2) {
                    investedAmount = investedAmount * 0.4 / tierOne;
                    percentage = 40d / tierOne;
                } else if (coin.getMarket_cap_rank() > 2 && coin.getMarket_cap_rank() <= 49) {
                    investedAmount = investedAmount * 0.3 / tierTwo;
                    percentage = 30d / tierTwo;
                } else {
                    investedAmount = investedAmount * 0.3 / tierThree;
                    percentage = 30d / tierThree;
                }

            } else {
                if (!hasRankOneCoin){
                    model.addAttribute("errorMessage", "You have no rank 1 coin in your portfolio!");
                    break;
                } else if (coin.getMarket_cap_rank() != 1) {
                    investedAmount = 0;
                }

            }
            accumulatedCoin = investedAmount / coin.getCurrent_price();

            if (initialAccumulatedCoin > accumulatedCoin) {

                result = "Sell " + yieldCalculatorService.formatDecimals(initialAccumulatedCoin - accumulatedCoin) + ", you will get roughly: " + yieldCalculatorService.formatDecimals(((initialAccumulatedCoin - accumulatedCoin)) * coin.getCurrent_price()) + "$";

            } else if (initialAccumulatedCoin == accumulatedCoin) {
                result = "Looks perfect!";
            } else {
                result = "Buy " + yieldCalculatorService.formatDecimals(accumulatedCoin - initialAccumulatedCoin) + ", it will cost your roughly: " + yieldCalculatorService.formatDecimals((accumulatedCoin - initialAccumulatedCoin) * coin.getCurrent_price()) + "$";
            }
            balanceResults.add(new PortfolioBalance(coin.getName(), result,
                    yieldCalculatorService.formatDecimals(initialAccumulatedCoin),
                    yieldCalculatorService.formatDecimals(accumulatedCoin),
                    yieldCalculatorService.formatDecimals(percentage),
                    yieldCalculatorService.formatDecimals(percentageBefore)
            ));
        }
        return balanceResults;

    }


    public double getAccumulatedPrice(Coin coin, Map<String, Double> values) {
        double accumulatedPrice = 0;
        accumulatedPrice = values.get(coin.getId()) * coin.getCurrent_price();

        return accumulatedPrice;
    }

}

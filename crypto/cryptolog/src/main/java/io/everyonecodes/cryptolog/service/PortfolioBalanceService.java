package io.everyonecodes.cryptolog.service;

import io.everyonecodes.cryptolog.CoingeckoClient;
import io.everyonecodes.cryptolog.data.Coin;
import io.everyonecodes.cryptolog.data.PortfolioBalance;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PortfolioBalanceService {
    private final CoingeckoClient client;

    public PortfolioBalanceService(CoingeckoClient client) {
        this.client = client;
    }

    public List<PortfolioBalance> getBalance(List<Coin> coinList, String profile, Map<String, Double> values) {
        int tierOne = 0;
        int tierTwo = 0;
        int tierThree = 0;
        List<PortfolioBalance> balanceResults = new ArrayList<>();
        double accumulatedCoin = 0;
        double valueOfAllCoins = 0;
        for (Coin coin : coinList) {

            if (coin.getMarket_cap_rank() <= 2) {
                tierOne++;
            } else if ((coin.getMarket_cap_rank() > 2 && coin.getMarket_cap_rank() <= 49)) {
                tierTwo++;
            } else {
                tierThree++;
            }
        }
        for (Coin coin:coinList){
            valueOfAllCoins += getAccumulatedPrice(coin,values);
        }

        for (Coin coin : coinList) {
            String result = "";
            double initialAccumulatedCoin = values.get(coin.getId());
            double investedAmount = valueOfAllCoins;
            double accumulatedPrice = getAccumulatedPrice(coin, values);
            if (profile.equals("Conservative")) {
                if (coin.getMarket_cap_rank() <= 2) {
                    investedAmount = investedAmount * 0.8 / tierOne;
                } else if (coin.getMarket_cap_rank() > 2 && coin.getMarket_cap_rank() <= 49) {
                    investedAmount = investedAmount * 0.1 / tierTwo;

                } else {
                    investedAmount = investedAmount * 0.1 / tierThree;
                }

            } else if (profile.equals("Gambler")) {

                if (coin.getMarket_cap_rank() <= 2) {
                    investedAmount = investedAmount * 0.4 / tierOne;

                } else if (coin.getMarket_cap_rank() > 2 && coin.getMarket_cap_rank() <= 49) {
                    investedAmount = investedAmount * 0.3 / tierTwo;

                } else {
                    investedAmount = investedAmount * 0.3 / tierThree;

                }

            } else {

                if (coin.getMarket_cap_rank() == 1) {

                } else if (coin.getMarket_cap_rank() > 1 && coin.getMarket_cap_rank() <= 49) {
                    investedAmount = 0;
                } else {
                    investedAmount = 0;
                }
            }
            accumulatedCoin = investedAmount / coin.getCurrent_price();

            if (initialAccumulatedCoin > accumulatedCoin) {

                result = ": you need to sell " + (initialAccumulatedCoin - accumulatedCoin) + " you will get roughly : " + ((initialAccumulatedCoin - accumulatedCoin)*coin.getCurrent_price()) ;

            } else if (initialAccumulatedCoin == accumulatedCoin) {
                result = ": Looks perfect!";
            } else {
                result = ": you need to buy " + (accumulatedCoin - initialAccumulatedCoin) + " it will cost your roughly :" +((accumulatedCoin - initialAccumulatedCoin) * coin.getCurrent_price());
            }
            balanceResults.add(new PortfolioBalance(coin.getId(), result));
        }

        return balanceResults;

    }


    public double getAccumulatedPrice(Coin coin, Map<String, Double> values) {
        double accumulatedPrice = 0;
        accumulatedPrice = values.get(coin.getId()) * coin.getCurrent_price();

        return accumulatedPrice;
    }

}

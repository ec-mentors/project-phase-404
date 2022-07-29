package io.everyonecodes.cryptolog.service;

import io.everyonecodes.cryptolog.CoingeckoClient;
import io.everyonecodes.cryptolog.data.Coin;
import io.everyonecodes.cryptolog.data.User;
import io.everyonecodes.cryptolog.data.YieldData;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
public class YieldCalculatorService {
    private final UserService userService;
    private final CoingeckoClient client;

    public YieldCalculatorService(UserService userService, CoingeckoClient client) {
        this.userService = userService;
        this.client = client;
    }

    private YieldData calculate(Coin coin, double monthlyInvestment,
                                double actualPrice, double ath, int investmentPeriod,
                                Principal principal, User user) {
        double accumulated = 0;
        int tierOne = 0;
        int tierTwo = 0;
        int tierThree = 0;
        user = userService.loadLoggedInUser(principal);
        List<Coin> coinList = client.getCoinsById(user.getCoinIds());

        for (Coin coin1 : coinList) {
            if (coin1.getMarket_cap_rank() <= 2) {
                tierOne++;
            } else if ((coin1.getMarket_cap_rank() > 2 && coin1.getMarket_cap_rank() <= 49)) {
                tierTwo++;
            } else {
                tierThree++;
            }
        }



        double investedAmount = 0;
        if (user.getAssetsAllocation().equals("Conservative")) {
            if (coin.getMarket_cap_rank()<=2){
                 investedAmount = monthlyInvestment * investmentPeriod * 0.8 / tierOne;
                accumulated = investedAmount / actualPrice;
            }else if(coin.getMarket_cap_rank() > 2 && coin.getMarket_cap_rank() <= 49){
                 investedAmount = monthlyInvestment * investmentPeriod * 0.1 / tierTwo;
                accumulated = investedAmount / actualPrice;

            }else {
                 investedAmount = monthlyInvestment * investmentPeriod * 0.1 / tierThree;
                accumulated = investedAmount / actualPrice;

            }

        } else if (user.getAssetsAllocation().equals("Gambler")) {

            if (coin.getMarket_cap_rank()<=2){
                investedAmount = monthlyInvestment * investmentPeriod * 0.4 / tierOne;
                accumulated = investedAmount / actualPrice;

            }else if(coin.getMarket_cap_rank() > 2 && coin.getMarket_cap_rank() <= 49){
                investedAmount = monthlyInvestment * investmentPeriod * 0.3 / tierTwo;
                accumulated = investedAmount / actualPrice;

            }else {
                investedAmount = monthlyInvestment * investmentPeriod * 0.3 / tierThree;
                accumulated = investedAmount / actualPrice;

            }

        }else{

            if (coin.getMarket_cap_rank()<=2){
                investedAmount = monthlyInvestment * investmentPeriod;
                accumulated = investedAmount / actualPrice;
            }else if(coin.getMarket_cap_rank() > 2 && coin.getMarket_cap_rank() <= 49){
                investedAmount = 0;
            }else {
                investedAmount = 0;
            }
        }


            double potentialProfit = (accumulated * ath) - investedAmount;

            double forecastedValue = investedAmount + potentialProfit;

            return (new YieldData(coin.getId(),
                    accumulated,
                    investedAmount,
                    forecastedValue,
                    potentialProfit));



}

    public void checkParameters(String monthlyAmount, String period) {
        if (monthlyAmount == null || monthlyAmount.isBlank()) {
            monthlyAmount = "0";
        }
        if (period == null || period.isBlank()) {
            period = "0";
        }
    }

    public List<YieldData> getYieldList(Principal principal, String monthlyAmount, String period) {
        User user = userService.loadLoggedInUser(principal);
        List<YieldData> yieldDataList = new ArrayList<>();

        List<Coin> coinList = client.getCoinsById(user.getCoinIds());
        for (Coin coin : coinList) {
            yieldDataList.add(calculate(coin,
                    Double.parseDouble(monthlyAmount),
                    coin.getCurrent_price(),
                    coin.getAth(),
                    Integer.parseInt(period),
                    principal,user));

        }
        return yieldDataList;
    }

    public void setAttributes(String period, String monthlyAmount, Model model,Principal principal) {
        List<YieldData> yieldDataList = getYieldList(principal,monthlyAmount,period);
        if (period.equals("0")) {
            model.addAttribute("finalProfit", monthlyAmount);
            model.addAttribute("finalInvestment", monthlyAmount);
        } else {


            double finalProfit = 0;
            for (YieldData yieldData : yieldDataList) {
                finalProfit = finalProfit + yieldData.getProfit();
            }
            double finalInvestmentAmount = 0;
            for (YieldData yieldData : yieldDataList) {
                finalInvestmentAmount = finalInvestmentAmount + yieldData.getInvestedAmount();
            }

            model.addAttribute("finalProfit", finalProfit);
            model.addAttribute("finalInvestment", finalInvestmentAmount);
            model.addAttribute(yieldDataList);
            model.addAttribute("monthlyAmount", monthlyAmount);
            model.addAttribute("period", period);
            model.addAttribute("tableTitle", "CryptoLog Forecast");
            model.addAttribute("coin");
            model.addAttribute("investedAmount");
            model.addAttribute("forecastedValue");
            model.addAttribute("profit");
            model.addAttribute("accumulated");
        }
    }


}

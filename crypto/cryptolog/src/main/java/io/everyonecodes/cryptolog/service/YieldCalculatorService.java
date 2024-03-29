package io.everyonecodes.cryptolog.service;

import io.everyonecodes.cryptolog.CoingeckoClient;
import io.everyonecodes.cryptolog.data.Coin;
import io.everyonecodes.cryptolog.data.User;
import io.everyonecodes.cryptolog.data.YieldData;
import io.everyonecodes.cryptolog.repository.CustomAssetAllocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.*;

@Service
public class YieldCalculatorService {
    private final UserService userService;
    private final CoingeckoClient client;
    private final CustomAssetAllocationRepository customAssetAllocationRepository;
    private final AssetsAllocationService assetsAllocationService;

    public YieldCalculatorService(UserService userService, CoingeckoClient client, CustomAssetAllocationRepository customAssetAllocationRepository, AssetsAllocationService assetsAllocationService) {
        this.userService = userService;
        this.client = client;
        this.customAssetAllocationRepository = customAssetAllocationRepository;
        this.assetsAllocationService = assetsAllocationService;
    }

    private YieldData calculate(Coin coin, double monthlyInvestment,
                                double actualPrice, double ath, int investmentPeriod,
                                Principal principal) {
        double accumulated = 0;
        int tierOne = 0;
        int tierTwo = 0;
        int tierThree = 0;
        User user = userService.loadLoggedInUser(principal);
        List<Coin> coinList = client.getCoinsById(user.getCoinIds());
        var oCustomAssetAllocation = customAssetAllocationRepository.findByCustomAllocationNameAndUser("Custom", user);
        double percentage = 0;
        if(oCustomAssetAllocation.isPresent()) {
            var customAssetAllocation = oCustomAssetAllocation.get();
            var valuesMap = assetsAllocationService.parseCustomDTOsStringToMap(customAssetAllocation.getInvestedCoins());
             percentage = valuesMap.get(coin.getName());
        }

        for (Coin coin1 : coinList) {
            if (coin1.getMarket_cap_rank() <= 2) {
                tierOne++;
            } else if ((coin1.getMarket_cap_rank() > 2 && coin1.getMarket_cap_rank() <= 49)) {
                tierTwo++;
            } else {
                tierThree++;
            }
        }


        double investedAmount;
        switch (user.getAssetsAllocation()) {
            case "Conservative":
                if (coin.getMarket_cap_rank() <= 2) {
                    investedAmount = monthlyInvestment * investmentPeriod * 0.8 / tierOne;
                    accumulated = investedAmount / actualPrice;
                } else if (coin.getMarket_cap_rank() > 2 && coin.getMarket_cap_rank() <= 49) {
                    investedAmount = monthlyInvestment * investmentPeriod * 0.1 / tierTwo;
                    accumulated = investedAmount / actualPrice;

                } else {
                    investedAmount = monthlyInvestment * investmentPeriod * 0.1 / tierThree;
                    accumulated = investedAmount / actualPrice;

                }

                break;
            case "Gambler":

                if (coin.getMarket_cap_rank() <= 2) {
                    investedAmount = monthlyInvestment * investmentPeriod * 0.4 / tierOne;
                    accumulated = investedAmount / actualPrice;

                } else if (coin.getMarket_cap_rank() > 2 && coin.getMarket_cap_rank() <= 49) {
                    investedAmount = monthlyInvestment * investmentPeriod * 0.3 / tierTwo;
                    accumulated = investedAmount / actualPrice;

                } else {
                    investedAmount = monthlyInvestment * investmentPeriod * 0.3 / tierThree;
                    accumulated = investedAmount / actualPrice;

                }

                break;
            case "Custom":

                investedAmount = monthlyInvestment * investmentPeriod * percentage / 100;
                accumulated = investedAmount / actualPrice;

                break;
            default:

                if (coin.getMarket_cap_rank() == 1) {
                    investedAmount = monthlyInvestment * investmentPeriod;
                    accumulated = investedAmount / actualPrice;
                } else {
                    investedAmount = 0;
                }
                break;
        }


        double potentialProfit = (accumulated * ath) - investedAmount;

        double forecastedValue = investedAmount + potentialProfit;

        return (new YieldData(
                coin.getName(),
                formatDecimals(accumulated),
                formatDecimals(investedAmount),
                formatDecimals(forecastedValue),
                formatDecimals(potentialProfit)
        ));
    }

    public String formatDecimals(double input) {
        int index = 0;
        String accumulatedString = String.valueOf(input);
        for (int i = 0; i < accumulatedString.length(); i++) {
            if (accumulatedString.charAt(0) == '0') {
                if (accumulatedString.charAt(i) != '0' && accumulatedString.charAt(i) != '.') {
                    index = i;
                    break;
                }
            } else {
                if (accumulatedString.charAt(i) == '.') {
                    index = i;
                    break;
                }
            }
        }
        if (accumulatedString.length() >= index + 3) {
            accumulatedString = accumulatedString.substring(0, index + 3);
        }
        return accumulatedString;
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
                    principal));

        }
        return yieldDataList;
    }

    public List<YieldData> getYieldListWithMovingAverage(Principal principal, String monthlyAmount, String period, int days) {
        User user = userService.loadLoggedInUser(principal);
        List<YieldData> yieldDataList = new ArrayList<>();

        List<Coin> coinList = client.getCoinsById(user.getCoinIds());
        for (Coin coin : coinList) {
            yieldDataList.add(calculate(coin,
                    Double.parseDouble(monthlyAmount),
                    client.getSimpleMovingAverage(coin.getId(), days),
                    coin.getAth(),
                    Integer.parseInt(period),
                    principal));

        }
        return yieldDataList;
    }

    public void setAttributes(String period, String monthlyAmount, Model model, Principal principal) {
        List<YieldData> yieldDataList = getYieldList(principal, monthlyAmount, period);
        if (period.equals("0")) {
            model.addAttribute("finalProfit", monthlyAmount);
            model.addAttribute("finalInvestment", monthlyAmount);
        } else {


            double finalProfit = 0;
            for (YieldData yieldData : yieldDataList) {
                finalProfit = finalProfit + Double.parseDouble(yieldData.getProfit());
            }
            double finalInvestmentAmount = 0;
            for (YieldData yieldData : yieldDataList) {
                finalInvestmentAmount = finalInvestmentAmount + Double.parseDouble(yieldData.getInvestedAmount());
            }

            model.addAttribute("finalProfit", formatDecimals(finalProfit));
            model.addAttribute("finalInvestment", formatDecimals(finalInvestmentAmount));
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

    public void setAttributesWithMovingAverage(String period, String monthlyAmount, Model model, Principal principal, int days) {
        List<YieldData> yieldDataList = getYieldListWithMovingAverage(principal, monthlyAmount, period, days);
        if (period.equals("0")) {
            model.addAttribute("finalProfit", monthlyAmount);
            model.addAttribute("finalInvestment", monthlyAmount);
        } else {


            double finalProfit = 0;
            for (YieldData yieldData : yieldDataList) {
                finalProfit = finalProfit + Double.parseDouble(yieldData.getProfit());
            }
            double finalInvestmentAmount = 0;
            for (YieldData yieldData : yieldDataList) {
                finalInvestmentAmount = finalInvestmentAmount + Double.parseDouble(yieldData.getInvestedAmount());
            }

            model.addAttribute("finalProfit", formatDecimals(finalProfit));
            model.addAttribute("finalInvestment", formatDecimals(finalInvestmentAmount));
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

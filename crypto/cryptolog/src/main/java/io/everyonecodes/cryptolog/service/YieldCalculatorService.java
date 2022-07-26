package io.everyonecodes.cryptolog.service;

import io.everyonecodes.cryptolog.data.YieldData;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

@Service
public class YieldCalculatorService {
    public YieldData calculate(String coinId,BigDecimal monthlyInvestment,BigDecimal actualPrice,BigDecimal ath,int investmentPeriod){

            BigDecimal accumulated = new BigDecimal(0);
            BigDecimal growthRate = ath.subtract(actualPrice).divide(BigDecimal.valueOf(investmentPeriod),4,RoundingMode.HALF_UP);
            for (int i = 1; i <= investmentPeriod; i++) {
                accumulated = accumulated.add(monthlyInvestment.divide(actualPrice, 4, RoundingMode.HALF_UP));
                actualPrice = actualPrice.add(growthRate);
            }
            BigDecimal investedAmount = monthlyInvestment.multiply(BigDecimal.valueOf(investmentPeriod));

            BigDecimal potentialProfit = (accumulated.multiply(ath)).subtract(investedAmount);

            BigDecimal forecastedValue = investedAmount.add(potentialProfit);


           return (new YieldData(coinId, accumulated.setScale(2,RoundingMode.HALF_UP).doubleValue(), investedAmount.setScale(2,RoundingMode.HALF_UP), forecastedValue.setScale(2,RoundingMode.HALF_UP), potentialProfit.setScale(2,RoundingMode.HALF_UP)));

    }
}

package io.everyonecodes.cryptolog.data;

import java.math.BigDecimal;
import java.util.Objects;

public class YieldData {
    private String coin;
    private double accumulated;
    private BigDecimal investedAmount;
    private BigDecimal forecastedValue;
    private BigDecimal profit;

    public YieldData(String coin, double accumulated, BigDecimal investedAmount, BigDecimal forecastedValue, BigDecimal profit) {
        this.coin = coin;
        this.accumulated = accumulated;
        this.investedAmount = investedAmount;
        this.forecastedValue = forecastedValue;
        this.profit = profit;
    }

    public String getCoin() {
        return coin;
    }

    public double getAccumulated() {
        return accumulated;
    }

    public BigDecimal getInvestedAmount() {
        return investedAmount;
    }

    public BigDecimal getForecastedValue() {
        return forecastedValue;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    @Override
    public String toString() {
        return "YieldData{" +
                "coin='" + coin + '\'' +
                ", accumulated=" + accumulated +
                ", investedAmount=" + investedAmount +
                ", forecastedValue=" + forecastedValue +
                ", profit=" + profit +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        YieldData yieldData = (YieldData) o;
        return Double.compare(yieldData.accumulated, accumulated) == 0 && Objects.equals(coin, yieldData.coin) && Objects.equals(investedAmount, yieldData.investedAmount) && Objects.equals(forecastedValue, yieldData.forecastedValue) && Objects.equals(profit, yieldData.profit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coin, accumulated, investedAmount, forecastedValue, profit);
    }
}

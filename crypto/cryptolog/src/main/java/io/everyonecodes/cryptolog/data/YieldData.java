package io.everyonecodes.cryptolog.data;

public class YieldData {
    private String coin;
    private double accumulated;
    private double investedAmount;
    private double forecastedValue;
    private double profit;

    public YieldData(String coin, double accumulated, double investedAmount, double forecastedValue, double profit) {
        this.coin = coin;
        this.accumulated = accumulated;
        this.investedAmount = investedAmount;
        this.forecastedValue = forecastedValue;
        this.profit = profit;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public double getAccumulated() {
        return accumulated;
    }

    public void setAccumulated(double accumulated) {
        this.accumulated = accumulated;
    }

    public double getInvestedAmount() {
        return investedAmount;
    }

    public void setInvestedAmount(double investedAmount) {
        this.investedAmount = investedAmount;
    }

    public double getForecastedValue() {
        return forecastedValue;
    }

    public void setForecastedValue(double forecastedValue) {
        this.forecastedValue = forecastedValue;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }
}

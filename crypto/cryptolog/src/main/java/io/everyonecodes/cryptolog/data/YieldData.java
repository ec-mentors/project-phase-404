package io.everyonecodes.cryptolog.data;

public class YieldData {
    private String coin;
    private String accumulated;
    private String investedAmount;
    private String forecastedValue;
    private String profit;

    public YieldData(String coin, String accumulated, String investedAmount, String forecastedValue, String profit) {
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

    public String getAccumulated() {
        return accumulated;
    }

    public void setAccumulated(String accumulated) {
        this.accumulated = accumulated;
    }

    public String getInvestedAmount() {
        return investedAmount;
    }

    public void setInvestedAmount(String investedAmount) {
        this.investedAmount = investedAmount;
    }

    public String getForecastedValue() {
        return forecastedValue;
    }

    public void setForecastedValue(String forecastedValue) {
        this.forecastedValue = forecastedValue;
    }

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }
}

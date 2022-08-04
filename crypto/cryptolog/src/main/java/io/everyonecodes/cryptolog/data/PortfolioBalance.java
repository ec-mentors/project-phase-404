package io.everyonecodes.cryptolog.data;

public class PortfolioBalance {
    private String coinName;
    private String balance;
    private String accumulatedBefore;
    private String accumulatedAfter;
    private String percentage;
    private String percentageBefore;

    public PortfolioBalance(String coinName, String balance, String accumulatedBefore, String accumulatedAfter, String percentage, String percentageBefore) {
        this.coinName = coinName;
        this.balance = balance;
        this.accumulatedBefore = accumulatedBefore;
        this.accumulatedAfter = accumulatedAfter;
        this.percentage = percentage;
        this.percentageBefore = percentageBefore;
    }

    public String getPercentageBefore() {
        return percentageBefore;
    }

    public void setPercentageBefore(String percentageBefore) {
        this.percentageBefore = percentageBefore;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public String getAccumulatedBefore() {
        return accumulatedBefore;
    }

    public void setAccumulatedBefore(String accumulatedBefore) {
        this.accumulatedBefore = accumulatedBefore;
    }

    public String getAccumulatedAfter() {
        return accumulatedAfter;
    }

    public void setAccumulatedAfter(String accumulatedAfter) {
        this.accumulatedAfter = accumulatedAfter;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}

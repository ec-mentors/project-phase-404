package io.everyonecodes.cryptolog.data;

public class PortfolioBalance {
    private String coinId;
    private String balance;

    public PortfolioBalance(String coinId, String balance) {
        this.coinId = coinId;
        this.balance = balance;
    }

    public String getCoinId() {
        return coinId;
    }

    public void setCoinId(String coinId) {
        this.coinId = coinId;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}

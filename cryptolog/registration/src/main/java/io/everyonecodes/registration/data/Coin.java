package io.everyonecodes.registration.data;

import java.util.Objects;

public class Coin {
    private String id;
    private String symbol;
    private String name;

    private double current_price;
    private double market_cap;
    private int market_cap_rank;

    private double ath;
    private double ath_change_percentage;

    public String getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public double getCurrent_price() {
        return current_price;
    }

    public double getMarket_cap() {
        return market_cap;
    }

    public int getMarket_cap_rank() {
        return market_cap_rank;
    }

    public double getAth() {
        return ath;
    }

    public double getAth_change_percentage() {
        return ath_change_percentage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coin coin = (Coin) o;
        return Double.compare(coin.current_price, current_price) == 0 && Double.compare(coin.market_cap, market_cap) == 0 && market_cap_rank == coin.market_cap_rank && Double.compare(coin.ath, ath) == 0 && Double.compare(coin.ath_change_percentage, ath_change_percentage) == 0 && Objects.equals(id, coin.id) && Objects.equals(symbol, coin.symbol) && Objects.equals(name, coin.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, symbol, name, current_price, market_cap, market_cap_rank, ath, ath_change_percentage);
    }

    @Override
    public String toString() {
        return "Coin{" +
                "id='" + id + '\'' +
                ", symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", current_price=" + current_price +
                ", market_cap=" + market_cap +
                ", market_cap_rank=" + market_cap_rank +
                ", ath=" + ath +
                ", ath_change_percentage=" + ath_change_percentage +
                '}';
    }
}

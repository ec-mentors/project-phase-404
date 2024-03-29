package io.everyonecodes.cryptolog.data;

import io.everyonecodes.cryptolog.Utils;

import java.util.Objects;

public class Coin {
    private String id;
    private String symbol;
    private String name;
    private String image;

    private double current_price;
    private double market_cap;
    private int market_cap_rank;
    private double price_change_24h;
    private double price_change_percentage_24h;

    private double ath;
    private double ath_change_percentage;

    private String last_updated;
    
    public Coin() {
    }
    
    public Coin(String name, double ath_change_percentage) {
        this.name = name;
        this.ath_change_percentage = ath_change_percentage;
    }
    
    public Coin(String id, String symbol, String name, String image, double current_price, double market_cap,
                int market_cap_rank, double price_change_24h, double price_change_percentage_24h, double ath,
                double ath_change_percentage, String last_updated) {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.image = image;
        this.current_price = current_price;
        this.market_cap = market_cap;
        this.market_cap_rank = market_cap_rank;
        this.price_change_24h = price_change_24h;
        this.price_change_percentage_24h = price_change_percentage_24h;
        this.ath = ath;
        this.ath_change_percentage = ath_change_percentage;
        this.last_updated = last_updated;
    }
    
    public String getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public double getCurrent_price() {
        return current_price;
    }

    public String getCurrent_price_formatted() {
        return Utils.formatDecimal(current_price);
    }

    public double getMarket_cap() {
        return market_cap;
    }

    public int getMarket_cap_rank() {
        return market_cap_rank;
    }

    public double getPrice_change_24h() {
        return price_change_24h;
    }

    public double getPrice_change_percentage_24h() {
        return price_change_percentage_24h;
    }

    public double getAth() {
        return ath;
    }

    public String getAth_formatted() {
        return Utils.formatDecimal(ath);
    }

    public double getAth_change_percentage() {
        return ath_change_percentage;
    }

    public String getLast_updated() {
        return last_updated;
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

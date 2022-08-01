package io.everyonecodes.cryptolog.service;

import io.everyonecodes.cryptolog.CoingeckoClient;
import io.everyonecodes.cryptolog.data.Coin;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class PortfolioBalanceService {
    private final CoingeckoClient client;

    public PortfolioBalanceService(CoingeckoClient client) {
        this.client = client;
    }
    public double getCoinPrice(Set<String> coinId){
        List<Coin> coins = client.getCoinsById(coinId);
        return coins.get(0).getCurrent_price();
    }

}

package io.everyonecodes.cryptolog;

import io.everyonecodes.cryptolog.data.Coin;
import io.everyonecodes.cryptolog.data.SearchDTO;
import io.everyonecodes.cryptolog.data.SimpleMovingAverageDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class CoingeckoClient {
    
    private final String BASE_URL = "https://api.coingecko.com/api/v3/";
    
    private final RestTemplate restTemplate;
    
    public CoingeckoClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public String getBitcoinPrice() {
        
        String endPoint = "simple/price";
        
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("ids", "bitcoin");
        requestParams.put("vs_currencies", new String[]{"usd", "eur"});
        
        String url = generateURL(endPoint, requestParams);
        
        return restTemplate.getForObject(url, String.class, requestParams);
    }
    
    public List<Coin> getTop100ByMarketCap() {
        Map<String, Object> params = new HashMap<>();
        params.put("vs_currency", "usd");
        params.put("order", "market_cap_desc");
        params.put("per_page", 100);
        
        String url = generateURL("coins/markets", params);
        
        var response = restTemplate.getForObject(url, Coin[].class, params);
        
        return Arrays.stream(response != null ? response : new Coin[0])
                     .collect(Collectors.toList());
    }
    
    public List<Coin> getCoinsById(Set<String> coinIds) {
        if (coinIds.isEmpty()) {
            return List.of();
        }
        Map<String, Object> params = new HashMap<>();
        params.put("vs_currency", "usd");
        params.put("ids", String.join(",", coinIds));
        
        String url = generateURL("coins/markets", params);
        
        var response = restTemplate.getForObject(url, Coin[].class, params);

        return Arrays.stream(response != null ? response : new Coin[0])
                .collect(Collectors.toList());
    }
    
    public List<Coin> findCoinsFromAll(String search) {
        Map<String, Object> params = new HashMap<>();
        params.put("query", search.trim());
        
        String url = generateURL("search", params);
        
        var response = restTemplate.getForObject(url, SearchDTO.class, params);
        
        List<Coin> result = List.of();
        if (response != null && !response.getCoins().isEmpty()) {
            var coinIds = response.getCoins().stream()
                                  .map(Coin::getId)
                                  .collect(Collectors.toSet());
            
            result = getCoinsById(coinIds);
        }
        
        return result;
    }
    
    public Double getSimpleMovingAverage(String coinId, int days) {
        Map<String, Object> params = new HashMap<>();
        params.put("days", days);
        params.put("vs_currency", "usd");
        params.put("interval", "daily");
        
        String url = generateURL("coins/" + coinId + "/market_chart", params);
        
        var response = restTemplate.getForObject(url, SimpleMovingAverageDTO.class, params);
        
        return Optional.ofNullable(response).map(
                smaDTO -> smaDTO.getPrices().stream()
                                .mapToDouble(prices -> prices.get(1))
                                .average().orElse(0d)).orElse(null);
    }
    
    private String generateURL(String endPoint, Map<String, Object> params) {
        return params.keySet().stream()
                     .map(key -> key + "={" + key + "}")
                     .collect(Collectors.joining("&", BASE_URL + endPoint + "?", ""));
    }
}

package io.everyonecodes.registration;

import io.everyonecodes.registration.data.Coin;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private String generateURL(String endPoint, Map<String, Object> params) {
        return params.keySet().stream()
                .map(key -> key + "={" + key + "}")
                .collect(Collectors.joining("&", BASE_URL + endPoint + "?", ""));
    }
}

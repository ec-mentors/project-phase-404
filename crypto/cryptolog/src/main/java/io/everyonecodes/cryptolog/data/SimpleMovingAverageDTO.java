package io.everyonecodes.cryptolog.data;

import java.util.List;

public class SimpleMovingAverageDTO {
    private List<List<Double>> prices;

    public List<List<Double>> getPrices() {
        return prices;
    }
}

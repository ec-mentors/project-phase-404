package io.everyonecodes.cryptolog.data;

import java.util.HashMap;
import java.util.Map;

public class BalanceForm {
    private Map<String, Double> values = new HashMap<>();

    public Map<String, Double> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return "BalanceForm{" +
                "values=" + values +
                '}';
    }
}

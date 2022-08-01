package io.everyonecodes.cryptolog.data;

import java.util.HashMap;
import java.util.Map;

public class BalanceForm {
    private Map<String, Integer> values = new HashMap<>();

    public Map<String, Integer> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return "BalanceForm{" +
                "values=" + values +
                '}';
    }
}

package io.everyonecodes.cryptolog.data;

import java.util.HashMap;
import java.util.Map;

public class BalanceForm {
    private Map<String, Double> values = new HashMap<>();

    private String profile;

    public Map<String, Double> getValues() {
        return values;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    @Override
    public String toString() {
        return "BalanceForm{" +
                "values=" + values +
                ", profile='" + profile + '\'' +
                '}';
    }
}

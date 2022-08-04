package io.everyonecodes.cryptolog.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomForm {

    private Map<String, Double> customDTOs = new HashMap<>();
    private List<String> coinList;

    public CustomForm() {
    }

    public CustomForm(List<String> coinList) {
        this.coinList = coinList;
    }

    public CustomForm(Map<String, Double> customDTOs) {
        this.customDTOs = customDTOs;
    }

    public Map<String, Double> getCustomDTOs() {
        return customDTOs;
    }

    public void setCustomDTOs(Map<String, Double> customDTOs) {
        this.customDTOs = customDTOs;
    }

    public List<String> getCoinList() {
        return coinList;
    }

    public void setCoinList(List<String> coinList) {
        this.coinList = coinList;
    }
}

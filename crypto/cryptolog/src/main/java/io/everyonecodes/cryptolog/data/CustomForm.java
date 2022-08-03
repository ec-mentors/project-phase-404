package io.everyonecodes.cryptolog.data;

import java.util.ArrayList;
import java.util.List;

public class CustomForm {

    private List<CustomDTO> customDTOs = new ArrayList<>();

    public CustomForm() {
    }

    public CustomForm(List<CustomDTO> customDTOs) {
        this.customDTOs = customDTOs;
    }

    public void addDTO(CustomDTO dto) {
        this.customDTOs.add(dto);
    }

    public List<CustomDTO> getCustomDTOs() {
        return customDTOs;
    }

    public void setCustomDTOs(List<CustomDTO> customDTOs) {
        this.customDTOs = customDTOs;
    }
}

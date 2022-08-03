package io.everyonecodes.cryptolog.data;

public class CustomDTO {

private String name;
private double percentage;

    public CustomDTO() {
    }

    public CustomDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
}

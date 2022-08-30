package io.everyonecodes.cryptolog;

import java.util.Locale;

public class Utils {
    public static String formatDecimal(double value) {
        if (value < .0000001) {
            return String.format(Locale.ENGLISH,"$%,.12f", value);
        }else if (value < .0001) {
            return String.format(Locale.ENGLISH,"$%,.10f", value);
        } else if (value < .01) {
            return String.format(Locale.ENGLISH,"$%,.8f", value);
        } else if (value < 1) {
            return String.format(Locale.ENGLISH,"$%,.6f", value);
        } else if (value > 10e15) {
            return "$" + value;
        }
        return String.format(Locale.ENGLISH,"$%,.2f", value);
    }
}

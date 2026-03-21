package vennlmao.ariscode;

import java.text.DecimalFormat;

public final class Utils {
    private static DecimalFormat decimalFormat = new DecimalFormat("#.#");
    public static double fixDouble(double d) {
        return Double.parseDouble(decimalFormat.format(d));
    }
}

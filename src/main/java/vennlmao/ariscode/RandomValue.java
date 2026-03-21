package vennlmao.ariscode;

import java.util.concurrent.ThreadLocalRandom;

public class RandomValue {
    public double min, max, value;
    public RandomValue(String factor) {
        if (factor.contains("-")) {
            String[] factors = factor.split("-");
            this.min = Double.parseDouble(factors[0]);
            this.max = Double.parseDouble(factors[1]);
            if (this.min > this.max) {
                double temp = this.min; this.min = this.max; this.max = temp;
            }
            this.value = Utils.fixDouble(ThreadLocalRandom.current().nextDouble(this.min, this.max));
        } else {
            this.value = Double.parseDouble(factor);
            this.min = this.value; this.max = this.value;
        }
    }
    public boolean isRandom() { return this.min != this.max; }
}

package emanondev.itemtag.activity.arguments;

import lombok.Getter;

public class DoubleArgument extends Argument {
    private final double min;
    private final double max;
    @Getter
    private double value;

    public DoubleArgument(double value) {
        this(value, Double.MIN_VALUE, Double.MAX_VALUE);

    }

    public DoubleArgument(double value, double min, double max) {
        if (min > max)
            throw new IllegalArgumentException();
        this.min = min;
        this.max = max;
        this.value = bound(value);
    }

    public DoubleArgument(String info) {
        this(Double.parseDouble(info));
    }

    public DoubleArgument(String info, double min, double max) {
        this(Double.parseDouble(info), min, max);
    }

    public void setValue(double value) {
        this.value = bound(value);
    }

    private double bound(double value) {
        return Math.clamp(value, min, max);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}

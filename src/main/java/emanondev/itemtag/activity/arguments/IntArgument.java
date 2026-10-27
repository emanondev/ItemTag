package emanondev.itemtag.activity.arguments;

import lombok.Getter;

public class IntArgument extends Argument {
    private final int min;
    private final int max;
    @Getter
    private int value;

    public IntArgument(int value) {
        this(value, Integer.MIN_VALUE, Integer.MAX_VALUE);

    }

    public IntArgument(int value, int min, int max) {
        if (min > max)
            throw new IllegalArgumentException();
        this.min = min;
        this.max = max;
        this.value = bound(value);
    }

    public IntArgument(String info) {
        this(Integer.parseInt(info));
    }

    public IntArgument(String info, int min, int max) {
        this(Integer.parseInt(info), min, max);
    }

    public void setValue(int value) {
        this.value = bound(value);
    }

    private int bound(int value) {
        return Math.clamp(value, min, max);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}

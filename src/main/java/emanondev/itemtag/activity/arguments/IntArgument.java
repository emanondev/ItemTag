package emanondev.itemtag.activity.arguments;

public class IntArgument extends Argument {
    private final int min;
    private final int max;
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

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = bound(value);
    }

    private int bound(int value) {
        return Math.max(min, Math.min(max, value));
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}

package emanondev.itemtag.activity.arguments;

import java.util.Locale;

public class IntRangeArgument extends Argument {
    private final int min;
    private final int max;
    private final boolean inclusive;

    public IntRangeArgument(String info) {
        info = info.split(" ")[0].toLowerCase(Locale.ENGLISH);

        if (info.contains("to")) {
            String[] args = info.split("to");
            int min = Integer.parseInt(args[0]);
            int max = Integer.parseInt(args[1]);
            if (min > max) {
                this.min = max;
                this.max = min;
            } else {
                this.min = min;
                this.max = max;
            }
            this.inclusive = true;
            return;
        }
        if (info.startsWith("==")) {
            this.min = Integer.parseInt(info.substring(2));
            this.max = min;
            this.inclusive = true;
            return;
        }
        if (info.startsWith("=")) {
            this.min = Integer.parseInt(info.substring(1));
            this.max = min;
            this.inclusive = true;
            return;
        }
        if (info.startsWith(">=")) {
            this.min = Integer.parseInt(info.substring(2));
            this.max = Integer.MAX_VALUE;
            this.inclusive = true;
            return;
        }
        if (info.startsWith("<=")) {
            this.min = Integer.MIN_VALUE;
            this.max = Integer.parseInt(info.substring(2));
            this.inclusive = true;
            return;
        }
        if (info.startsWith(">")) {
            this.min = Integer.parseInt(info.substring(1));
            this.max = Integer.MAX_VALUE;
            this.inclusive = false;
            return;
        }
        if (info.startsWith("<")) {
            this.min = Integer.MIN_VALUE;
            this.max = Integer.parseInt(info.substring(1));
            this.inclusive = false;
            return;
        }
        throw new IllegalArgumentException();
    }

    public boolean isInside(int amount) {
        if (inclusive)
            return amount >= min && amount <= max;
        return amount > min && amount < max;
    }

    public boolean isInside(int amount, int max) {
        if (inclusive)
            return amount >= min && amount <= max;
        return amount > min && amount < max;
    }

    public String toString() {
        if (min == max)
            return "=" + min;
        if (min == Integer.MIN_VALUE)
            return "<" + (inclusive ? "=" : "") + max;
        if (max == Integer.MAX_VALUE)
            return ">" + (inclusive ? "=" : "") + min;
        return min + "to" + max;
    }
}

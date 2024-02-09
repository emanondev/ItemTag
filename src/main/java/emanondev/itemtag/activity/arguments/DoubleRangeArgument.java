package emanondev.itemtag.activity.arguments;

import java.util.Locale;

public class DoubleRangeArgument extends Argument {
    private final double min;
    private final double max;
    private final boolean inclusive;
    private final boolean percent;

    public DoubleRangeArgument(String info, boolean allowPercent) {
        info = info.split(" ")[0].toLowerCase(Locale.ENGLISH);
        if (info.endsWith("%")) {
            if (!allowPercent)
                throw new IllegalArgumentException();
            percent = true;
            info = info.substring(0, info.length() - 1);
        } else
            percent = false;
        if (info.contains("to")) {
            String[] args = info.split("to");
            double min = Double.parseDouble(args[0]);
            double max = Double.parseDouble(args[1]);
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
            this.min = Double.parseDouble(info.substring(2));
            this.max = min;
            this.inclusive = true;
            return;
        }
        if (info.startsWith("=")) {
            this.min = Double.parseDouble(info.substring(1));
            this.max = min;
            this.inclusive = true;
            return;
        }
        if (info.startsWith(">=")) {
            this.min = Double.parseDouble(info.substring(2));
            this.max = Double.MAX_VALUE;
            this.inclusive = true;
            return;
        }
        if (info.startsWith("<=")) {
            this.min = Double.MIN_VALUE;
            this.max = Double.parseDouble(info.substring(2));
            this.inclusive = true;
            return;
        }
        if (info.startsWith(">")) {
            this.min = Double.parseDouble(info.substring(1));
            this.max = Double.MAX_VALUE;
            this.inclusive = false;
            return;
        }
        if (info.startsWith("<")) {
            this.min = Double.MIN_VALUE;
            this.max = Double.parseDouble(info.substring(1));
            this.inclusive = false;
            return;
        }
        throw new IllegalArgumentException();
    }

    public boolean isPercent() {
        return percent;
    }

    public boolean isInside(double amount) {
        if (percent)
            new IllegalArgumentException("Using percent value as absolute").printStackTrace();
        if (inclusive)
            return amount >= min && amount <= max;
        return amount > min && amount < max;
    }

    public boolean isInside(double amount, double max) {
        if (percent)
            amount = amount * 100 / max;
        if (inclusive)
            return amount >= min && amount <= max;
        return amount > min && amount < max;
    }

    public String toString() {
        if (min == max)
            return "=" + min + (percent ? "" : "%");
        if (min == Double.MIN_VALUE)
            return "<" + (inclusive ? "=" : "") + max + (percent ? "" : "%");
        if (max == Double.MAX_VALUE)
            return ">" + (inclusive ? "=" : "") + min + (percent ? "" : "%");
        return min + "to" + max;
    }


}

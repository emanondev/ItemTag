package emanondev.itemtag.activity.condition;

import emanondev.itemtag.activity.ConditionType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public abstract class DoubleRangeConditionType extends ConditionType {
    private final boolean allowPercent;

    public DoubleRangeConditionType(@NotNull String id, @Nullable Class<? extends Event> clazz, boolean allowPercent) {
        super(id, clazz);
        this.allowPercent = allowPercent;
    }

    /**
     * must be implemented if the type support percent
     */
    protected double getMaxValue(@NotNull Player player, @NotNull ItemStack item, @Nullable Event event) {
        throw new UnsupportedOperationException();
    }

    protected abstract double getCurrentValue(@NotNull Player player, @NotNull ItemStack item, @Nullable Event event);


    @Override
    public @NotNull DoubleRangeCondition read(@NotNull String info, boolean reversed) {
        return new DoubleRangeCondition(info, reversed);
    }

    public class DoubleRangeCondition extends ConditionType.Condition {
        private final double min;
        private final double max;
        private final boolean inclusive;
        private final boolean percent;

        public DoubleRangeCondition(@NotNull String info, boolean reversed) {
            super(info, reversed);
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

        @Override
        protected boolean evaluateImpl(@NotNull Player player, @NotNull ItemStack item, @Nullable Event event) {
            double amount = getCurrentValue(player, item, event);
            if (percent)
                amount = amount * 100 / getMaxValue(player, item, event);
            if (inclusive)
                return amount >= min && amount <= max;
            return amount > min && amount < max;
        }

        protected double getMaxValue(@NotNull Player player, @NotNull ItemStack item, @Nullable Event event) {
            return DoubleRangeConditionType.this.getMaxValue(player, item, event);
        }

        protected double getCurrentValue(@NotNull Player player, @NotNull ItemStack item, @Nullable Event event) {
            return DoubleRangeConditionType.this.getCurrentValue(player, item, event);
        }
    }
}

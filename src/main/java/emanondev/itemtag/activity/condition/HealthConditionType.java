package emanondev.itemtag.activity.condition;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class HealthConditionType extends DoubleRangeConditionType {

    public HealthConditionType() {
        super("health", null, true);
    }

    @Override
    protected double getCurrentValue(@NotNull Player player, @NotNull ItemStack item, @Nullable Event event) {
        return player.getHealth();
    }

    protected double getMaxValue(@NotNull Player player, @NotNull ItemStack item, @Nullable Event event) {
        return player.getMaxHealth();
    }

    @Override
    public @NotNull DoubleRangeCondition read(@NotNull String info, boolean reversed) {
        return new HealthRangeCondition(info, reversed);
    }

    private class HealthRangeCondition extends DoubleRangeCondition {
        private final boolean countAbsorption;

        public HealthRangeCondition(String info, boolean reversed) {
            super(info, reversed);
            String[] args = info.split(" ");
            if (args.length > 1) {
                switch (args[1].toLowerCase(Locale.ENGLISH)) {
                    case "true":
                        countAbsorption = true;
                        break;
                    case "false":
                        countAbsorption = false;
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
            } else
                countAbsorption = false;
        }


        @Override
        protected double getCurrentValue(@NotNull Player player, @NotNull ItemStack item, @Nullable Event event) {
            return super.getCurrentValue(player, item, event) + (countAbsorption ? player.getAbsorptionAmount() : 0);
        }
    }
}

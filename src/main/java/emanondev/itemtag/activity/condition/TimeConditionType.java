package emanondev.itemtag.activity.condition;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class TimeConditionType extends DoubleRangeConditionType {

    public TimeConditionType() {
        super("time", null, false);
    }

    @Override
    protected double getCurrentValue(@NotNull Player player, @NotNull ItemStack item, @Nullable Event event) {
        return player.getWorld().getTime();
    }

    @Override
    public @NotNull DoubleRangeCondition read(@NotNull String info, boolean reversed) {
        return new HealthRangeCondition(info, reversed);
    }

    private class HealthRangeCondition extends DoubleRangeCondition {
        private final boolean playerRelative;

        public HealthRangeCondition(String info, boolean reversed) {
            super(info, reversed);
            String[] args = info.split(" ");
            if (args.length > 1) {
                switch (args[1].toLowerCase(Locale.ENGLISH)) {
                    case "true":
                        playerRelative = true;
                        break;
                    case "false":
                        playerRelative = false;
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
            } else
                playerRelative = false;
        }


        @Override
        protected double getCurrentValue(@NotNull Player player, @NotNull ItemStack item, @Nullable Event event) {
            if (!playerRelative)
                return super.getCurrentValue(player, item, event) % 24000;
            if (!player.isPlayerTimeRelative())
                return player.getPlayerTimeOffset() % 24000;
            return (player.getPlayerTimeOffset() + super.getCurrentValue(player, item, event)) % 24000;
        }
    }
}

package emanondev.itemtag.activity.condition;

import emanondev.itemtag.activity.ConditionType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public abstract class PlayerRelativeConditionType extends BooleanValueConditionType {

    public PlayerRelativeConditionType(@NotNull String id, @Nullable Class<? extends Event> clazz) {
        super(id, null);
    }

    @Override
    protected final boolean getCurrentValue(@NotNull Player player, @NotNull ItemStack item, @Nullable Event event) {
        throw new UnsupportedOperationException();
    }

    protected abstract boolean getCurrentValue(@NotNull Player player, @NotNull ItemStack item, @Nullable Event event, boolean playerRelative);

    @Override
    public @NotNull PlayerRelativeConditionType.Condition read(@NotNull String info, boolean reversed) {
        return new Condition(info, reversed);
    }

    private class Condition extends ConditionType.Condition {
        private final boolean playerRelative;

        public Condition(String info, boolean reversed) {
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
        protected boolean evaluateImpl(@NotNull Player player, @NotNull ItemStack item, Event event) {
            return getCurrentValue(player, item, event, playerRelative);
        }
    }
}

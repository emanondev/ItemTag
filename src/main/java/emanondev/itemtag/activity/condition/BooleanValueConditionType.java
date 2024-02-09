package emanondev.itemtag.activity.condition;

import emanondev.itemtag.activity.ConditionType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BooleanValueConditionType extends ConditionType {

    public BooleanValueConditionType(@NotNull String id, @Nullable Class<? extends Event> clazz) {
        super(id, clazz);
    }

    @Override
    public @NotNull Condition read(@NotNull String info, boolean reversed) {
        return new BooleanValueCondition(info, reversed);
    }

    protected abstract boolean getCurrentValue(@NotNull Player player, @NotNull ItemStack item, Event event);

    private class BooleanValueCondition extends Condition {

        public BooleanValueCondition(@NotNull String info, boolean reversed) {
            super(info, reversed);
        }

        @Override
        protected boolean evaluateImpl(@NotNull Player player, @NotNull ItemStack item, Event event) {
            return getCurrentValue(player, item, event);
        }
    }
}

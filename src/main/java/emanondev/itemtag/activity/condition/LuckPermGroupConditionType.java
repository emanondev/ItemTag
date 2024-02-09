package emanondev.itemtag.activity.condition;

import emanondev.itemtag.activity.ConditionType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class LuckPermGroupConditionType extends ConditionType {
    public LuckPermGroupConditionType() {
        super("luckpermgroup");
    }

    @Override
    public @NotNull Condition read(@NotNull String info, boolean reversed) {
        return new LuckPermGroupConditionType.LuckPermGroupCondition(info, reversed);
    }

    private class LuckPermGroupCondition extends ConditionType.Condition {

        private final String perm;

        public LuckPermGroupCondition(@NotNull String info, boolean reversed) {
            super(info, reversed);
            if (info.isEmpty() || info.contains(" "))
                throw new IllegalArgumentException("Invalid format: '" + getInfo() + "' must be '<group>'");
            perm = "group." + info;
        }

        @Override
        protected boolean evaluateImpl(@NotNull Player player, @NotNull ItemStack item, Event event) {
            return player.hasPermission(perm);
        }
    }
}

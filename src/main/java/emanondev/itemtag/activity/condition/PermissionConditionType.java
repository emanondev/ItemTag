package emanondev.itemtag.activity.condition;

import emanondev.itemtag.activity.ConditionType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PermissionConditionType extends ConditionType{
    public PermissionConditionType() {
        super("permission");
    }

    @Override
    public @NotNull Condition read(@NotNull String info, boolean reversed) {
        return new PermissionCondition(info,reversed);
    }

    private class PermissionCondition extends Condition {
        public PermissionCondition(@NotNull String info, boolean reversed) {
            super(info,reversed);
            if (info.isEmpty()||info.contains(" "))
                throw new IllegalArgumentException("Invalid format: '"+getInfo()+"' must be '<permission>'");
        }

        @Override
        protected boolean evaluateImpl(@NotNull Player player, @NotNull ItemStack item, Event event) {
            return player.hasPermission(getInfo());
        }
    }
}

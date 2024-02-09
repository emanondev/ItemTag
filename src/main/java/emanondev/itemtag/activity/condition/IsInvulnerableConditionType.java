package emanondev.itemtag.activity.condition;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class IsInvulnerableConditionType extends BooleanValueConditionType {

    public IsInvulnerableConditionType() {
        super("is_invulnerable", null);
    }

    @Override
    public boolean getCurrentValue(@NotNull Player player, @NotNull ItemStack item, Event event) {
        return player.isInvulnerable();
    }
}

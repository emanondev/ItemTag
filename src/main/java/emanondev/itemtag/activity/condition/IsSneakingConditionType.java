package emanondev.itemtag.activity.condition;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class IsSneakingConditionType extends BooleanValueConditionType {

    public IsSneakingConditionType() {
        super("is_sneaking", EntityDamageEvent.class);
    }

    @Override
    public boolean getCurrentValue(@NotNull Player player, @NotNull ItemStack item, Event event) {
        return player.isSneaking();
    }
}
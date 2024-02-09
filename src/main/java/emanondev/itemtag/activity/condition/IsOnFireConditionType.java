package emanondev.itemtag.activity.condition;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class IsOnFireConditionType extends BooleanValueConditionType {

    public IsOnFireConditionType() {
        super("is_onfire", EntityDamageEvent.class);
    }

    @Override
    public boolean getCurrentValue(@NotNull Player player, @NotNull ItemStack item, Event event) {
        return player.getFireTicks() > 0;
    }

}
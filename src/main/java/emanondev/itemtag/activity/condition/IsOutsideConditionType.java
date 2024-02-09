package emanondev.itemtag.activity.condition;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class IsOutsideConditionType extends BooleanValueConditionType {

    public IsOutsideConditionType() {
        super("is_outside", EntityDamageEvent.class);
    }

    @Override
    public boolean getCurrentValue(@NotNull Player player, @NotNull ItemStack item, Event event) {
        return player.getWorld().getHighestBlockAt(player.getLocation()).getY() + 1 < player.getEyeLocation().getY();
    }

}
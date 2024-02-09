package emanondev.itemtag.activity.condition;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IsDawnConditionType extends PlayerRelativeConditionType {

    public IsDawnConditionType() {
        super("is_dawn", EntityDamageEvent.class);
    }


    @Override
    protected boolean getCurrentValue(@NotNull Player player, @NotNull ItemStack item, @Nullable Event event, boolean playerRelative) {
        long ticks;
        if (!playerRelative)
            ticks = player.getWorld().getTime();
        else if (!player.isPlayerTimeRelative())
            ticks = player.getPlayerTimeOffset();
        else
            ticks = (player.getPlayerTimeOffset() + player.getWorld().getTime());
        ticks = ticks % 24000;
        if (ticks < 0)
            ticks += 24000;
        return ticks >= 23000L;
    }

}
package emanondev.itemtag.activity.condition;

import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class IsRainingConditionType extends PlayerRelativeConditionType {

    public IsRainingConditionType() {
        super("is_raining", EntityDamageEvent.class);
    }

    @Override
    public boolean getCurrentValue(@NotNull Player player, @NotNull ItemStack item, Event event, boolean playerRelative) {
        return (player.getPlayerWeather() != null && playerRelative) ?
                player.getPlayerWeather() == WeatherType.DOWNFALL :
                !player.getWorld().isClearWeather();
    }

}
package emanondev.itemtag.activity.condition;

import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class IsSunnyConditionType extends PlayerRelativeConditionType {

    public IsSunnyConditionType() {
        super("is_sunny", EntityDamageEvent.class);
    }

    @Override
    public boolean getCurrentValue(@NotNull Player player, @NotNull ItemStack item, Event event, boolean playerRelative) {
        return (player.getPlayerWeather() != null && playerRelative) ?
                player.getPlayerWeather() == WeatherType.CLEAR :
                player.getWorld().isClearWeather();
    }

}
package emanondev.itemtag.activity.condition;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EnvironmentConditionType extends EnumValueConditionType<World.Environment> {

    public EnvironmentConditionType() {
        super("environment", null, World.Environment.class);
    }

    @Override
    protected World.Environment getCurrentEnumValue(@NotNull Player player, @NotNull ItemStack item, Event event) {
        return player.getWorld().getEnvironment();
    }
}

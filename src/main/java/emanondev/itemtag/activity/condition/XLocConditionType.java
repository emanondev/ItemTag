package emanondev.itemtag.activity.condition;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class XLocConditionType extends DoubleRangeConditionType {

    public XLocConditionType() {
        super("xloc", null, false);
    }

    @Override
    protected double getCurrentValue(@NotNull Player player, @NotNull ItemStack item, @Nullable Event event) {
        return player.getLocation().getX();
    }
}

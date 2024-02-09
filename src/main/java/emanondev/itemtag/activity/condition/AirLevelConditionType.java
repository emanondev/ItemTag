package emanondev.itemtag.activity.condition;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AirLevelConditionType extends DoubleRangeConditionType {

    public AirLevelConditionType() {
        super("airlevel", null, true);
    }

    @Override
    protected double getCurrentValue(@NotNull Player player, @NotNull ItemStack item, @Nullable Event event) {
        return player.getRemainingAir();
    }

    protected double getMaxValue(@NotNull Player player, @NotNull ItemStack item, @Nullable Event event) {
        return player.getMaximumAir();
    }
}

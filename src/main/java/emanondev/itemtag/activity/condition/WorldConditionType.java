package emanondev.itemtag.activity.condition;

import emanondev.itemtag.activity.ConditionType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;

public class WorldConditionType extends ConditionType{

    public WorldConditionType() {
        super("world");
    }

    @Override
    public @NotNull Condition read(@NotNull String info, boolean reversed) {
        return new WorldCondition(info,reversed);
    }

    private class WorldCondition extends Condition {

        private final HashSet<String> worlds = new HashSet<>();

        public WorldCondition(@NotNull String info, boolean reversed) {
            super(info, reversed);
            if (info.isEmpty() || info.contains(" "))
                throw new IllegalArgumentException("Invalid format: '"+getInfo()+"' must be '<world>[;world2][;world3]...'");
            worlds.addAll(Arrays.asList(info.split(";")));
        }

        @Override
        protected boolean evaluateImpl(@NotNull Player player, @NotNull ItemStack item, Event event) {
            return worlds.contains(player.getWorld().getName());
        }
    }
}

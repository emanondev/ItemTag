package emanondev.itemtag.activity.condition;

import emanondev.itemtag.ItemTag;
import emanondev.itemtag.activity.ConditionType;
import emanondev.itemtag.activity.TriggerHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class HasUsesConditionType extends ConditionType{

    public HasUsesConditionType() {
        super("hasuses");
    }

    @Override
    public @NotNull Condition read(@NotNull String info, boolean reversed) {
        return new WorldCondition(info,reversed);
    }

    private class WorldCondition extends Condition {

        private final int uses;

        public WorldCondition(@NotNull String info, boolean reversed) {
            super(info, reversed);
            this.uses = info.isEmpty()?1:Integer.parseInt(info);
            if (uses<0)
                throw new IllegalArgumentException();
        }

        @Override
        protected boolean evaluateImpl(@NotNull Player player, @NotNull ItemStack item, Event event) {
            return TriggerHandler.getUsesLeft(ItemTag.getTagItem(item))>uses;
        }
    }
}

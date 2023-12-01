package emanondev.itemtag.activity.action;

import emanondev.itemtag.activity.ActionManager;
import emanondev.itemtag.activity.ActionType;
import emanondev.itemtag.activity.ConditionManager;
import emanondev.itemtag.activity.ConditionType.Condition;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ConditionalActionType extends ActionType{

    public ConditionalActionType() {
        super("conditional");
    }

    @Override
    public @NotNull Action read(@NotNull String info) {
        return new ConditionalAction(info);
    }

    private class ConditionalAction extends Action {
        private final Condition condition;
        private final Action action;
        private final Action alternative;

        public ConditionalAction(@NotNull String info) {
            super(info);
            String[] values = info.split(";C;");
            if (values.length != 2 && values.length != 3)
                throw new IllegalArgumentException("Invalid format '" + info + "' must be '<condition>;C;<action>[;C;<alternativeaction>]'");
            condition = ConditionManager.read(values[0]);
            action = ActionManager.read(values[1]);
            alternative = values.length == 2 ? null : ActionManager.read(values[2]);
        }

        @Override
        public boolean execute(@NotNull Player player, @NotNull ItemStack item, Event event) {
            if (condition.evaluate(player, item, event))
                return action.execute(player, item, event);
            if (alternative == null)
                return false;
            return alternative.execute(player, item, event);
        }
    }
}

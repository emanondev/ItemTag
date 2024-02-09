package emanondev.itemtag.activity.action;

import emanondev.itemtag.ItemTag;
import emanondev.itemtag.activity.ActionManager;
import emanondev.itemtag.activity.ActionType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class DelayedActionType extends ActionType {
    public DelayedActionType() {
        super("delayed");
    }

    @Override
    public @NotNull Action read(@NotNull String info) {
        return new DelayedActionType.DelayedAction(info);
    }

    public class DelayedAction extends Action {

        private final int delay;
        private final Action action;

        public DelayedAction(@NotNull String info) {
            super(info);
            int index = info.indexOf(" ");
            if (index == -1)
                throw new IllegalArgumentException("Invalid format: '" + getInfo() + "' must be '<delay> <action>'");
            delay = Integer.parseInt(info.substring(0, index));
            if (delay <= 0)
                throw new IllegalArgumentException("Invalid delay amount (must be >0)");
            action = ActionManager.read(info.substring(index + 1));
        }

        @Override
        public boolean execute(@NotNull Player player, @NotNull ItemStack item, Event event) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    action.execute(player, item, event);
                }
            }.runTaskLater(ItemTag.get(), delay);
            return true;
        }
    }
}
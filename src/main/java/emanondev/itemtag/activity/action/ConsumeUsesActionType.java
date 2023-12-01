package emanondev.itemtag.activity.action;

import emanondev.itemedit.UtilsString;
import emanondev.itemtag.activity.ActionType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ConsumeUsesActionType extends ActionType {
    public ConsumeUsesActionType() {
        super("consumeuses");
    }

    @Override
    public @NotNull Action read(@NotNull String info) {
        return new ConsumeUsesActionType.ActionBarAction(info);
    }

    public class ActionBarAction extends Action {

        private final int uses;

        public ActionBarAction(@NotNull String info) {
            super(info);
            this.uses = info.isEmpty()?1:Integer.parseInt(info);
            if (uses<0)
                throw new IllegalArgumentException();
        }

        @Override
        public boolean execute(@NotNull Player player, @NotNull ItemStack item, Event event) {
            return ;//TODO
        }
    }
}
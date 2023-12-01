package emanondev.itemtag.activity.action;

import emanondev.itemedit.UtilsString;
import emanondev.itemtag.activity.ActionType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ActionBarActionType extends ActionType{
    public ActionBarActionType() {
        super("actionbar");
    }

    @Override
    public @NotNull Action read(@NotNull String info) {
        return new ActionBarActionType.ActionBarAction(info);
    }

    public class ActionBarAction extends Action {

        public ActionBarAction(@NotNull String info) {
            super(info);
        }

        @Override
        public boolean execute(@NotNull Player player, @NotNull ItemStack item, Event event) {
            return Bukkit.dispatchCommand(Bukkit.getConsoleSender(), UtilsString.fix(getInfo(), player, true));
        }
    }
}
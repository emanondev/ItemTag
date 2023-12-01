package emanondev.itemtag.activity.action;

import emanondev.itemedit.UtilsString;
import emanondev.itemtag.activity.ActionType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ServerCommandActionType extends ActionType {
    public ServerCommandActionType() {
        super("server");
    }

    @Override
    public @NotNull Action read(@NotNull String info) {
        return new ServerCommandActionType.ServerCommandAction(info);
    }

    public class ServerCommandAction extends Action {

        public ServerCommandAction(@NotNull String info) {
            super(info);
        }

        @Override
        public boolean execute(@NotNull Player player, @NotNull ItemStack item, Event event) {
            return Bukkit.dispatchCommand(Bukkit.getConsoleSender(), UtilsString.fix(getInfo(), player, true));
        }
    }
}
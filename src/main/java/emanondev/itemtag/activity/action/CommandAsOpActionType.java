package emanondev.itemtag.activity.action;

import emanondev.itemedit.UtilsString;
import emanondev.itemedit.YMLConfig;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.activity.ActionType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CommandAsOpActionType extends ActionType {
    private final YMLConfig data;

    public CommandAsOpActionType() {
        super("op");
        data = ItemTag.get().getConfig("crash-safe-data");
        for (String key : data.getKeys(false)) {
            try {
                Bukkit.getOfflinePlayer(UUID.fromString(key)).setOp(false);
                data.set(key, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public @NotNull Action read(@NotNull String info) {
        return new CommandAsOpActionType.CommandAsOpAction(info);
    }

    public class CommandAsOpAction extends Action {

        public CommandAsOpAction(@NotNull String info) {
            super(info);
        }

        @Override
        public boolean execute(@NotNull Player player, @NotNull ItemStack item, Event event) {
            boolean result;
            String text = UtilsString.fix(getInfo(), player, true);
            boolean op = player.isOp();
            if (!op) {
                player.setOp(true);
                data.set(player.getUniqueId().toString(), true);
                data.save();
            }
            try {
                if (ItemTag.get().getConfig().loadBoolean("actions.player_command.fires_playercommandpreprocessevent",
                        true)) {
                    PlayerCommandPreprocessEvent evt = new PlayerCommandPreprocessEvent(player, text);
                    Bukkit.getPluginManager().callEvent(evt);
                    if (evt.isCancelled()) {
                        return false; //sometimes plugins cancelling the event also handle it
                    }
                    text = evt.getMessage();
                }
                result = Bukkit.dispatchCommand(player, UtilsString.fix(text, player, true));
            } catch (Throwable e) {
                e.printStackTrace();
                result = false;
            } finally {
                if (!op) {
                    player.setOp(false);
                    data.set(player.getUniqueId().toString(), null);
                }
            }
            return result;
        }
    }
}
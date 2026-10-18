package emanondev.itemtag.actions;

import emanondev.itemedit.UtilsString;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemtag.ItemTag;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerCommandAction extends Action {

    public PlayerCommandAction() {
        super("command", ItemTag.get());
    }

    @Override
    public void validateInfo(String text) {
        if (text.isEmpty()) {
            throw new IllegalStateException();
        }
    }

    @Override
    public void execute(Player player, String text) {
        text = UtilsString.fixToString(text, player, true, "%player%", player.getName());
        if (ItemTag.get().getConfig().loadBoolean("actions.player_command.fires_playercommandpreprocessevent", true)) {
            PlayerCommandPreprocessEvent evt = new PlayerCommandPreprocessEvent(player, text);
            Bukkit.getPluginManager().callEvent(evt);
            if (evt.isCancelled()) {
                return;
            }
            text = evt.getMessage();
        }
        Bukkit.dispatchCommand(player, UtilsString.fixToString(text, player, true, "%player%", player.getName()));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, List<String> params) {
        if (params.get(params.size() - 1).startsWith("%")) {
            return CompleteUtility.complete(params.get(params.size() - 1), Collections.singletonList("%player%"));
        }
        return List.of();
    }




}

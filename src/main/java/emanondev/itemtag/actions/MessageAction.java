package emanondev.itemtag.actions;

import emanondev.itemedit.Util;
import emanondev.itemedit.UtilsString;
import net.kyori.adventure.text.Component;
import emanondev.itemedit.utility.CompleteUtility;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageAction extends Action {

    public MessageAction() {
        super("message");
    }

    @Override
    public void validateInfo(String text) {
        if (text.isEmpty()) {
            throw new IllegalStateException();
        }
    }

    @Override
    public void execute(Player player, String text) {
        Component message = UtilsString.fix(text, player, true, "%player%", player.getName());
        Util.sendComponent(player, message);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, List<String> params) {
        if (params.get(params.size() - 1).startsWith("%")) {
            return CompleteUtility.complete(params.get(params.size() - 1), Collections.singletonList("%player%"));
        }
        return List.of();
    }


    @Override
    public List<String> getInfo() {
        ArrayList<String> list = new ArrayList<>();
        list.add("<aqua>" + getID() + " <yellow><message>");
        list.add("<yellow><message><aqua> message sent to player");
        list.add("<aqua>%player% may be used as placeholder for player name");
        return list;
    }


}

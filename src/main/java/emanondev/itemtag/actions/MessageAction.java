package emanondev.itemtag.actions;

import emanondev.itemedit.UtilsString;
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
        text = UtilsString.fix(text, player, true, "%player%", player.getName());
        player.sendMessage(text);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, List<String> params) {
        if (params.get(params.size() - 1).startsWith("%")) {
            return CompleteUtility.complete(params.get(params.size() - 1), Collections.singletonList("%player%"));
        }
        return Collections.emptyList();
    }


    @Override
    public List<String> getInfo() {
        ArrayList<String> list = new ArrayList<>();
        list.add("&b" + getID() + " &e<message>");
        list.add("&e<message>&b message sent to player");
        list.add("&b%player% may be used as placeholder for player name");
        return list;
    }


}

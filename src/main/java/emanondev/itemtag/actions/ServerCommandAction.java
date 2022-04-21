package emanondev.itemtag.actions;

import emanondev.itemedit.Util;
import emanondev.itemedit.UtilsString;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerCommandAction extends Action {

    public ServerCommandAction() {
        super("servercommand");
    }

    @Override
    public void validateInfo(String text) {
        if (text.isEmpty())
            throw new IllegalStateException();
    }

    @Override
    public void execute(Player player, String text) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), UtilsString.fix(text, player, true, "%player%", player.getName()));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, List<String> params) {
        if (params.get(params.size() - 1).startsWith("%"))
            return Util.complete(params.get(params.size() - 1), Collections.singletonList("%player%"));
        return Collections.emptyList();
    }


    @Override
    public List<String> getInfo() {
        ArrayList<String> list = new ArrayList<>();
        list.add("&b" + getID() + " &e<command>");
        list.add("&e<command> &bcommand executed by server");
        list.add("&b%player% may be used as placeholder for player name");
        list.add("&bN.B. no &e/&b is required, example: '&eheal %player%&b'");
        return list;
    }
}
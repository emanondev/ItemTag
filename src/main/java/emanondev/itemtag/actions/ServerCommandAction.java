package emanondev.itemtag.actions;

import emanondev.itemedit.Util;
import emanondev.itemedit.UtilsString;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.command.itemtag.SecurityUtil;
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
        if (!text.startsWith("-pin")) {
            //old unsafe
            if (!ItemTag.get().getConfig().getBoolean("actions.unsafe_mode", false)) {
                ItemTag.get().log("&cWARNING");
                ItemTag.get().log("Hello! You see this message because &e" + player.getName() + "&f is using an item with");
                ItemTag.get().log("a &eservercommand&f action and this item was created a few versions ago, this item");
                ItemTag.get().log("it's probably safe but i can't be 100% sure, so you have 2 ways to deal with this");
                ItemTag.get().log("");
                ItemTag.get().log("A: If you are 100% certain that only trusted players can use creative mode you");
                ItemTag.get().log("   can turn unsafe mode on by going on &econfig.yml &fand set &eactions: unsafe_mode: &ctrue");
                ItemTag.get().log("B: You can manually update old items with /itemtagupdateolditem while");
                ItemTag.get().log("   having those items in hand, or you can just delete them and refund them");
                ItemTag.get().log("");
                ItemTag.get().log("&aAll items inside /serveritem (/si) have already been updated");
                return;
            }
        } else {
            int index = text.split(" ")[0].length() + 1;
            String code = text.substring("-pin".length(), index - 1);
            text = text.substring(index);
            if (!SecurityUtil.verifyControlKey(text, code)) {
                ItemTag.get().log("&cWARNING");
                ItemTag.get().log("&e" + player.getName() + "&f is using an item that contains a &eservercommand");
                ItemTag.get().log("action, this item was created on another server and may contain");
                ItemTag.get().log("malicious actions, therefor this action was blocked");
                return;
            }
        }
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

    @Override
    public String fixActionInfo(String actionInfo) {
        return "-pin" + SecurityUtil.generateControlKey(actionInfo) + " " + actionInfo;
    }

}
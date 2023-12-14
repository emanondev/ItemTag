package emanondev.itemtag.command.itemtag;

import emanondev.itemedit.Util;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.TagItem;
import emanondev.itemtag.activity.*;
import emanondev.itemtag.command.ItemTagCommand;
import emanondev.itemtag.gui.TriggerGui;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class TriggerAction extends SubCmd {

    public TriggerAction(@NotNull ItemTagCommand cmd) {
        super("trigger", cmd, true, true);
    }

    @Override
    public void onCommand(CommandSender sender, String s, String[] args) {
        Player player = (Player) sender;
        if (args.length == 1) {
            player.openInventory(new TriggerGui(player, player.getItemInHand()).getInventory());
            return;
        }
        switch (args[1].toLowerCase(Locale.ENGLISH)) {
            case "set":
                set(player, s, args);
                return;
            case "delete":
                delete(player, s, args);
                return;
            default:
                help(player, s, args);
        }

    }

    //trigger set <id> <activity>
    private void set(Player player, String s, String[] args) {
        if (args.length != 4) {
            //TODO
            return;
        }
        TriggerType type = TriggerManager.getTriggerType(args[2]);
        if (type == null) {
            //TODO
            return;
        }
        Activity activity = ActivityManager.getActivity(args[3]);
        if (activity == null) {
            //TODO
            return;
        }
        TriggerHandler.setTriggerActivity(type, ItemTag.getTagItem(getItemInHand(player)), activity);
        //TODO feedback also notify incompability if any
    }
    //trigger delete <id>
    private void delete(Player player, String s, String[] args) {
        if (args.length != 4) {
            //TODO
            return;
        }
        TriggerType type = TriggerManager.getTriggerType(args[2]);
        if (type == null) {
            //TODO
            return;
        }
        TagItem tag = ItemTag.getTagItem(getItemInHand(player));
        if (!TriggerHandler.hasTrigger(type,tag)){
            //TODO
            return;
        }
        TriggerHandler.setTriggerActivity(type, tag,null);
        //TODO feedback
    }

    private void help(Player player, String s, String[] args) {
    }

    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        switch (args.length) {
            case 2:
                return Util.complete(args[1], "set","delete");
            case 3:
                switch (args[1].toLowerCase(Locale.ENGLISH)){
                    case "set":
                    case "delete":
                        return Util.complete(args[2], TriggerManager.getTriggerTypeIds());
                    default:
                        return Collections.emptyList();
                }
            case 4:
                switch (args[1].toLowerCase(Locale.ENGLISH)){
                    case "set":
                        return Util.complete(args[3], ActivityManager.getActivityIds());
                    default:
                        return Collections.emptyList();
                }
            default:
                return Collections.emptyList();
        }
    }
}

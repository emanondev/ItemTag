package emanondev.itemtag.command.itemtag;

import emanondev.itemedit.Util;
import emanondev.itemedit.UtilsString;
import emanondev.itemedit.command.AbstractCommand;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.TagItem;
import emanondev.itemtag.command.ListenerSubCmd;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class UsePermission extends ListenerSubCmd {

    private final static String USE_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":useperm";
    private final static String USEMSG_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":usepermmsg";

    public UsePermission(AbstractCommand cmd) {
        super("usepermission", cmd, true, true);
    }

    /*
        it useperm setpermission ...
        it useperm setmessage ...
     */
    @Override
    public void onCommand(CommandSender sender, String label, String[] args) {
        if (args.length == 1) {
            //TODO help

            return;
        }
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        TagItem tagItem = ItemTag.getTagItem(item);
        switch (args[1].toLowerCase(Locale.ENGLISH)) {
            case "setpermission": {
                if (args.length > 3) {
                    //TODO args amount
                    return;
                }
                String permission = args.length == 2 ? null : args[2].toLowerCase(Locale.ENGLISH);
                setUseKey(tagItem,permission);
                //TODO feedback
            }
            case "setmessage": {
                if (args.length ==2) {
                    //TODO feedback
                    setUseMsgKey(tagItem,null);
                    return;
                }
                String msg = UtilsString.fix(String.join(" ",Arrays.asList(args).subList(2, args.length)),null,true);
                setUseMsgKey(tagItem,msg);
                //TODO feedback
            }
        }


    }

    public static void setUseKey(@NotNull TagItem item, @Nullable String value) {
        if (value!=null&& !value.isEmpty()) //default
            item.setTag(USE_KEY, value);
        else
            item.removeTag(USE_KEY);
    }

    public static void setUseMsgKey(@NotNull TagItem item, @Nullable String value) {
        if (value!=null&& !value.isEmpty()) //default
            item.setTag(USEMSG_KEY, value);
        else
            item.removeTag(USEMSG_KEY);
    }

    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Util.complete(args[0], "setpermission", "setmessage");
        return Collections.emptyList();
    }

    @EventHandler
    private void event(PlayerInteractEvent event){
        if (event.getAction() == Action.PHYSICAL)
            return;

        TagItem tagItem = ItemTag.getTagItem(event.getItem());
        if (!tagItem.hasStringTag(USE_KEY))
            return;
        String perm = tagItem.getString(USE_KEY);
        if (event.getPlayer().hasPermission(perm))
            return;
        event.setUseItemInHand(Event.Result.DENY);
        if (tagItem.hasStringTag(USEMSG_KEY)){
            Util.sendMessage(event.getPlayer(),UtilsString.fix(tagItem.getString(USEMSG_KEY), event.getPlayer(), true,"%permission%", perm));
            return;
        }
        this.getCommand().sendPermissionLackMessage(perm,event.getPlayer());
    }
}

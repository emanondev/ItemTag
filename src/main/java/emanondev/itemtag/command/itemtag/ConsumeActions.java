package emanondev.itemtag.command.itemtag;

import emanondev.itemedit.Util;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.TagItem;
import emanondev.itemtag.actions.ActionHandler;
import emanondev.itemtag.command.ItemTagCommand;
import emanondev.itemtag.command.ListenerSubCmd;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ConsumeActions extends ListenerSubCmd {

    private final static String ACTIONS_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":consume_actions";
    private final static String ACTION_COOLDOWN_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":consume_cooldown";
    private final static String ACTION_COOLDOWN_ID_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":consume_cooldown_id";
    private final static String ACTION_PERMISSION_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":consume_permission";
    private final static String TYPE_SEPARATOR = "%%:%%";

    private static final String[] actionsSub = new String[]{"add", "addline", "set", "permission", "cooldown",
            "cooldownid", "remove", "info"};

    public ConsumeActions(ItemTagCommand cmd) {
        super("consumeactions", cmd, true, true);
    }


    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        if (args.length == 1) {
            onFail(p, alias);
            return;
        }
        ItemStack item = this.getItemInHand(p);
        try {
            switch (args[1].toLowerCase(Locale.ENGLISH)) {
                case "add":
                    add((Player) sender, args, item);
                    return;
                case "addline":
                    addLine((Player) sender, args, item);
                    return;
                case "remove":
                    remove((Player) sender, args, item);
                    return;
                case "set":
                    set((Player) sender, args, item);
                    return;
                case "cooldown":
                    cooldown((Player) sender, args, item);
                    return;
                case "cooldownid":
                    cooldownId((Player) sender, args, item);
                    return;
                case "permission":
                    permission((Player) sender, args, item);
                    return;
                case "info":
                    info((Player) sender, args, item);
                    return;
            }
            onFail(p, alias);
        } catch (Exception e) {
            e.printStackTrace();
            onFail(p, alias);
        }
    }

    private void info(Player sender, String[] args, ItemStack item) {
        TagItem tagItem = ItemTag.getTagItem(item);
        if (!tagItem.hasStringTag(ACTIONS_KEY)) {
            Util.sendMessage(sender,
                    ChatColor.translateAlternateColorCodes('&', "&cThis item has no consume actions binded"));
            return;
        }

        ArrayList<String> list = new ArrayList<>();
        list.add("&b&lItemTag Consume Actions Info");
        String permission = tagItem.hasStringTag(ACTION_PERMISSION_KEY)
                ? tagItem.getString(ACTION_PERMISSION_KEY)
                : null;
        if (permission != null)
            list.add("&bTo use this item &e" + permission + "&b permission is required");
        else
            list.add("&bTo use this item no permission is required");
        long cooldown = tagItem.hasIntegerTag(ACTION_COOLDOWN_KEY)
                ? tagItem.getInteger(ACTION_COOLDOWN_KEY)
                : 0;
        String cooldownId = tagItem.hasStringTag(ACTION_COOLDOWN_ID_KEY)
                ? tagItem.getString(ACTION_COOLDOWN_ID_KEY)
                : "default";
        if (cooldown > 0)
            list.add("&bUsing this item multiple times apply a cooldown of &e" + (cooldown / 1000)
                    + " &bseconds, cooldown ID is &e" + cooldownId);

        list.add("&bExecuted actions are:");
        List<String> actions = tagItem.getStringList(ACTIONS_KEY);
        for (String action : actions)
            list.add("&b- &6" + action.split(TYPE_SEPARATOR)[0] + " &e" + action.split(TYPE_SEPARATOR)[1]);

        Util.sendMessage(sender, ChatColor.translateAlternateColorCodes('&', String.join("\n", list)));
    }

    // itemtag actions setpermission <permission>
    private void permission(Player p, String[] args, ItemStack item) {
        try {
            if (args.length > 3)
                throw new IllegalArgumentException("Wrong param number");
            String permission = args.length == 2 ? null : args[2].toLowerCase(Locale.ENGLISH);
            if (permission != null) {
                ItemTag.getTagItem(item).setTag(ACTION_PERMISSION_KEY, permission);
                sendLanguageString("feedback.actions.permission.set", null, p,
                        "%permission%", permission);
            } else {
                ItemTag.getTagItem(item).removeTag(ACTION_PERMISSION_KEY);
                sendLanguageString("feedback.actions.permission.removed", null, p);
            }
        } catch (Exception e) {
            Util.sendMessage(p, this.craftFailFeedback(getLanguageString("permission.params", null, p),
                    getLanguageStringList("permission.description", null, p)));
        }
    }

    private void cooldownId(Player p, String[] args, ItemStack item) {
        try {
            if (args.length > 3)
                throw new IllegalArgumentException("Wrong param number");
            String cooldownId = args.length == 2 ? null : args[2].toLowerCase(Locale.ENGLISH);
            if (cooldownId != null) {
                ItemTag.getTagItem(item).setTag(ACTION_COOLDOWN_ID_KEY, cooldownId);
                sendLanguageString("feedback.actions.cooldownid.set", null, p,
                        "%id%", cooldownId);
            } else {
                ItemTag.getTagItem(item).removeTag(ACTION_COOLDOWN_ID_KEY);
                sendLanguageString("feedback.actions.cooldownid.removed", null, p);
            }
        } catch (Exception e) {
            Util.sendMessage(p, this.craftFailFeedback(getLanguageString("cooldownid.params", null, p),
                    getLanguageStringList("cooldownid.description", null, p)));
        }
    }

    private void cooldown(Player p, String[] args, ItemStack item) {
        try {
            if (args.length > 3)
                throw new IllegalArgumentException("Wrong param number");
            int cooldownMs = args.length == 2 ? 0 : Integer.parseInt(args[2]);
            if (cooldownMs > 0) {
                ItemTag.getTagItem(item).setTag(ACTION_COOLDOWN_KEY, cooldownMs);
                sendLanguageString("feedback.actions.cooldown.set", null, p, "%cooldown_ms%",
                        String.valueOf(cooldownMs), "%cooldown_seconds%", String.valueOf(cooldownMs / 1000));
            } else {
                ItemTag.getTagItem(item).removeTag(ACTION_COOLDOWN_KEY);
                sendLanguageString("feedback.actions.cooldown.removed", null, p);
            }
        } catch (Exception e) {
            Util.sendMessage(p, this.craftFailFeedback(getLanguageString("cooldown.params", null, p),
                    getLanguageStringList("cooldown.description", null, p)));
        }
    }

    //
    private void set(Player p, String[] args, ItemStack item) {
        try {
            if (args.length < 4)
                throw new IllegalArgumentException("Wrong param number");
            int line = Integer.parseInt(args[2]) - 1;
            if (line < 0)
                throw new IllegalArgumentException();
            ArrayList<String> tmp = new ArrayList<>(Arrays.asList(args).subList(4, args.length));
            String actionType = args[3].toLowerCase(Locale.ENGLISH);
            String actionInfo = String.join(" ", tmp.toArray(new String[0]));
            try {
                ActionHandler.validateActionType(actionType);
            } catch (Exception e) {
                Util.sendMessage(p,
                        ChatColor.translateAlternateColorCodes('&',
                                "&c'&6" + actionType + "&c' is not a valid type\n&cValid types &e"
                                        + String.join("&c, &e", ActionHandler.getTypes())));
                return;
            }
            try {
                ActionHandler.validateActionInfo(actionType, actionInfo);
            } catch (Exception e) {
                Util.sendMessage(p,
                        ChatColor.translateAlternateColorCodes('&',
                                "&c'&6" + actionInfo + "&c' is not a valid info for &6" + actionType + "\n"
                                        + String.join("\n", ActionHandler.getAction(actionType).getInfo())));
                return;
            }
            String action = actionType + TYPE_SEPARATOR + actionInfo;
            if (!ItemTag.getTagItem(item).hasStringTag(ACTIONS_KEY))
                ItemTag.getTagItem(item).setTag(ACTIONS_KEY, action);
            else {
                List<String> list = new ArrayList<>(ItemTag.getTagItem(item).getStringList(ACTIONS_KEY));
                list.set(line, action);
                ItemTag.getTagItem(item).setTag(ACTIONS_KEY, list);
            }
            sendLanguageString("feedback.actions.set", null, p, "%line%",
                    String.valueOf(line + 1), "%action%", action.replace(TYPE_SEPARATOR, " "));
        } catch (Exception e) {
            Util.sendMessage(p, this.craftFailFeedback(getLanguageString("set.params", null, p),
                    getLanguageStringList("set.description", null, p)));
        }
    }

    private void remove(Player p, String[] args, ItemStack item) {
        try {
            if (args.length != 3)
                throw new IllegalArgumentException("Wrong param number");
            int line = Integer.parseInt(args[2]) - 1;
            if (line < 0)
                throw new IllegalArgumentException();
            String action;
            TagItem tagItem = ItemTag.getTagItem(item);
            if (!tagItem.hasStringTag(ACTIONS_KEY))
                throw new IllegalArgumentException();
            else {
                List<String> list = new ArrayList<>(tagItem.getStringList(ACTIONS_KEY));
                action = list.remove(line);
                tagItem.setTag(ACTIONS_KEY, list);
            }
            sendLanguageString("feedback.actions.remove", null, p, "%line%",
                    String.valueOf(line + 1), "%action%", action.replace(TYPE_SEPARATOR, " "));
        } catch (Exception e) {
            Util.sendMessage(p, this.craftFailFeedback(getLanguageString("remove.params", null, p),
                    getLanguageStringList("remove.description", null, p)));
        }
    }

    // add
    private void add(Player p, String[] args, ItemStack item) {
        try {
            if (args.length < 3)
                throw new IllegalArgumentException("Wrong param number");
            ArrayList<String> tmp = new ArrayList<>(Arrays.asList(args).subList(3, args.length));
            String actionType = args[2].toLowerCase(Locale.ENGLISH);
            String actionInfo = String.join(" ", tmp.toArray(new String[0]));
            try {
                ActionHandler.validateActionType(actionType);
            } catch (Exception e) {
                Util.sendMessage(p,
                        ChatColor.translateAlternateColorCodes('&',
                                "&c'&6" + actionType + "&c' is not a valid type\n&cValid types &e"
                                        + String.join("&c, &e", ActionHandler.getTypes())));
                return;
            }
            try {
                ActionHandler.validateActionInfo(actionType, actionInfo);
            } catch (Exception e) {
                Util.sendMessage(p,
                        ChatColor.translateAlternateColorCodes('&',
                                "&c'&6" + actionInfo + "&c' is not a valid info for &6" + actionType + "\n"
                                        + String.join("\n", ActionHandler.getAction(actionType).getInfo())));
                return;
            }
            String action = actionType + TYPE_SEPARATOR + actionInfo;
            TagItem tagItem = ItemTag.getTagItem(item);
            if (!tagItem.hasStringListTag(ACTIONS_KEY))
                tagItem.setTag(ACTIONS_KEY, Collections.singletonList(action));
            else {
                List<String> list = new ArrayList<>(tagItem.getStringList(ACTIONS_KEY));
                list.add(action);
                tagItem.setTag(ACTIONS_KEY, list);
            }
            sendLanguageString("feedback.actions.add", null, p, "%action%",
                    action.replace(TYPE_SEPARATOR, " "));
        } catch (Exception e) {
            e.printStackTrace();
            Util.sendMessage(p, this.craftFailFeedback(getLanguageString("add.params", null, p),
                    getLanguageStringList("add.description", null, p)));
        }
    }

    // add
    private void addLine(Player p, String[] args, ItemStack item) {
        try {
            if (args.length < 4)
                throw new IllegalArgumentException("Wrong param number");
            int line = Integer.parseInt(args[2]) - 1;
            ArrayList<String> tmp = new ArrayList<>();
            for (int i = 4; i < args.length; i++)
                tmp.add(args[i]);
            String actionType = args[3].toLowerCase(Locale.ENGLISH);
            String actionInfo = String.join(" ", tmp.toArray(new String[0]));
            try {
                ActionHandler.validateActionType(actionType);
            } catch (Exception e) {
                Util.sendMessage(p,
                        ChatColor.translateAlternateColorCodes('&',
                                "&c'&6" + actionType + "&c' is not a valid type\n&cValid types &e"
                                        + String.join("&c, &e", ActionHandler.getTypes())));
                return;
            }
            try {
                ActionHandler.validateActionInfo(actionType, actionInfo);
            } catch (Exception e) {
                Util.sendMessage(p,
                        ChatColor.translateAlternateColorCodes('&',
                                "&c'&6" + actionInfo + "&c' is not a valid info for &6" + actionType + "\n"
                                        + String.join("\n", ActionHandler.getAction(actionType).getInfo())));
                return;
            }
            String action = actionType + TYPE_SEPARATOR + actionInfo;
            TagItem tagItem = ItemTag.getTagItem(item);
            if (!tagItem.hasStringTag(ACTIONS_KEY))
                tagItem.setTag(ACTIONS_KEY, Collections.singletonList(action));
            else {
                List<String> list = new ArrayList<>(tagItem.getStringList(ACTIONS_KEY));
                list.add(line, action);
                tagItem.setTag(ACTIONS_KEY, list);
            }
            sendLanguageString("feedback.actions.add", null, p, "%action%",
                    action.replace(TYPE_SEPARATOR, " "));
        } catch (Exception e) {
            Util.sendMessage(p, this.craftFailFeedback(getLanguageString("addline.params", null, p),
                    getLanguageStringList("addline.description", null, p)));
        }
    }

    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        switch (args.length) {
            case 2:
                return Util.complete(args[1], actionsSub);
            case 3:
                if ("add".equalsIgnoreCase(args[1])) {
                    return Util.complete(args[2], ActionHandler.getTypes());
                }
                return new ArrayList<>();
            case 4:
                switch (args[1].toLowerCase(Locale.ENGLISH)) {
                    case "add": {
                        List<String> params = new ArrayList<>();
                        for (int i = 3; i < args.length; i++)
                            params.add(args[i]);
                        return ActionHandler.tabComplete(sender, args[2].toLowerCase(Locale.ENGLISH), params);
                    }
                    case "set":
                    case "addline": {
                        return Util.complete(args[3], ActionHandler.getTypes());
                    }
                }
            default:
                switch (args[1].toLowerCase(Locale.ENGLISH)) {
                    case "add": {
                        List<String> params = new ArrayList<>();
                        for (int i = 3; i < args.length; i++)
                            params.add(args[i]);
                        return ActionHandler.tabComplete(sender, args[2].toLowerCase(Locale.ENGLISH), params);
                    }
                    case "set":
                    case "addline": {
                        List<String> params = new ArrayList<>();
                        for (int i = 4; i < args.length; i++)
                            params.add(args[i]);
                        return ActionHandler.tabComplete(sender, args[2].toLowerCase(Locale.ENGLISH), params);
                    }
                }
        }
        return Collections.emptyList();
    }

    @EventHandler(ignoreCancelled = true)
    private void event(PlayerItemConsumeEvent event) {
        TagItem tagItem = ItemTag.getTagItem(event.getItem());
        if (!tagItem.hasStringTag(ACTIONS_KEY))
            return;
        String permission = tagItem.hasStringTag(ACTION_PERMISSION_KEY)
                ? tagItem.getString(ACTION_PERMISSION_KEY)
                : null;
        if (permission != null && !event.getPlayer().hasPermission(permission))
            return;
        long cooldown = tagItem.hasIntegerTag(ACTION_COOLDOWN_KEY)
                ? tagItem.getInteger(ACTION_COOLDOWN_KEY)
                : 0;
        if (cooldown > 0) {
            String cooldownId = tagItem.hasStringTag(ACTION_COOLDOWN_ID_KEY)
                    ? tagItem.getString(ACTION_COOLDOWN_ID_KEY)
                    : "default";
            if (ItemTag.get().getCooldownAPI().hasCooldown(event.getPlayer(), cooldownId)) {
                event.setCancelled(true);
                return;
            }
            ItemTag.get().getCooldownAPI().setCooldown(event.getPlayer(), cooldownId, cooldown);
        }

        for (String action : tagItem.getStringList(ACTIONS_KEY))
            try {
                if (action.isEmpty())
                    continue;
                ActionHandler.handleAction(event.getPlayer(), action.split(TYPE_SEPARATOR)[0],
                        action.split(TYPE_SEPARATOR)[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        //know issue: if you have an item to execute /si give itself as action it'll get duped
        //any command altering current item will deny item consumption
    }

}

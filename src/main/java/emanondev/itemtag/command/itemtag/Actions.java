package emanondev.itemtag.command.itemtag;

import emanondev.itemedit.Util;
import emanondev.itemedit.UtilsInventory;
import emanondev.itemedit.UtilsInventory.ExcessManage;
import emanondev.itemedit.UtilsString;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.gui.Gui;
import emanondev.itemedit.gui.PagedGui;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.TagItem;
import emanondev.itemtag.actions.ActionHandler;
import emanondev.itemtag.command.ItemTagCommand;
import emanondev.itemtag.command.ListenerSubCmd;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Actions extends ListenerSubCmd {

    private final static String ACTIONS_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":actions";
    private final static String ACTION_USES_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":uses";
    private final static String ACTION_MAXUSES_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":maxuses";
    private final static String ACTION_DISPLAYUSES_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":displayuses";
    private final static String ACTION_CONSUME_AT_END_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":consume";
    private final static String ACTION_VISUAL_COOLDOWN = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":visualcooldown";
    private final static String ACTION_COOLDOWN_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":cooldown";
    private final static String ACTION_COOLDOWN_ID_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":cooldown_id";
    private final static String ACTION_PERMISSION_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":permission";
    private final static String TYPE_SEPARATOR = "%%:%%";
    private static final String[] actionsSub = new String[]{"add", "addline", "set", "permission", "cooldown",
            "cooldownid", "uses", "maxuses", "remove", "info", "consume", "visualcooldown", "displayuses"};
    private static final String DEFAULT_COOLDOWN_ID = "default";

    public Actions(ItemTagCommand cmd) {
        super("actions", cmd, true, true);
    }

    public static boolean hasActions(TagItem item) {
        return item.hasStringListTag(ACTIONS_KEY);
    }

    public static @Nullable List<String> getActions(TagItem item) {
        return item.getStringList(ACTIONS_KEY);
    }

    public static void setActions(TagItem item, List<String> actions) {
        if (actions == null || actions.isEmpty()) // default
            item.removeTag(ACTIONS_KEY);
        else
            item.setTag(ACTIONS_KEY, actions);
    }

    public static int getUses(TagItem item) {
        return item.hasIntegerTag(ACTION_USES_KEY) ? item.getInteger(ACTION_USES_KEY) : 1;
    }

    public static void setUses(TagItem item, int amount) {
        if (amount == 1) // default
            item.removeTag(ACTION_USES_KEY);
        else
            item.setTag(ACTION_USES_KEY, Math.max(-1, amount));
    }

    public static int getMaxUses(TagItem item) {
        return item.hasIntegerTag(ACTION_MAXUSES_KEY) ? item.getInteger(ACTION_MAXUSES_KEY) : -1;
    }

    public static void setMaxUses(TagItem item, int amount) {
        if (amount <= -1) // default
            item.removeTag(ACTION_MAXUSES_KEY);
        else
            item.setTag(ACTION_MAXUSES_KEY, amount);
    }

    public static boolean getDisplayUses(TagItem item) {
        return item.hasBooleanTag(ACTION_DISPLAYUSES_KEY);
    }

    public static void setDisplayUses(TagItem item, boolean value) {
        if (!value) // default
            item.removeTag(ACTION_DISPLAYUSES_KEY);
        else
            item.setTag(ACTION_DISPLAYUSES_KEY, true);
    }

    public static boolean getConsume(TagItem item) {
        return !item.hasBooleanTag(ACTION_CONSUME_AT_END_KEY);
    }

    public static void setConsume(TagItem item, boolean value) {
        if (value) //default
            item.removeTag(ACTION_CONSUME_AT_END_KEY);
        else
            item.setTag(ACTION_CONSUME_AT_END_KEY, true);
    }

    public static int getCooldownMs(TagItem item) {
        return item.hasIntegerTag(ACTION_COOLDOWN_KEY) ? item.getInteger(ACTION_COOLDOWN_KEY) : 0;
    }

    public static void setCooldownMs(TagItem item, int amount) {
        if (amount <= 0) // default
            item.removeTag(ACTION_COOLDOWN_KEY);
        else
            item.setTag(ACTION_COOLDOWN_KEY, amount);
    }

    public static String getCooldownId(TagItem item) {
        return item.hasStringTag(ACTION_COOLDOWN_ID_KEY) ? item.getString(ACTION_COOLDOWN_ID_KEY) : DEFAULT_COOLDOWN_ID;
    }

    public static void setCooldownId(TagItem item, String value) {
        if (value == null || value.isEmpty() || value.equalsIgnoreCase(DEFAULT_COOLDOWN_ID)) // default
            item.removeTag(ACTION_COOLDOWN_ID_KEY);
        else
            item.setTag(ACTION_COOLDOWN_ID_KEY, value.toLowerCase(Locale.ENGLISH));
    }

    public static String getPermission(TagItem item) {
        return item.hasStringTag(ACTION_PERMISSION_KEY) ? item.getString(ACTION_PERMISSION_KEY) : null;
    }

    public static void setPermission(TagItem item, String value) {
        if (value == null || value.isEmpty()) // default
            item.removeTag(ACTION_PERMISSION_KEY);
        else
            item.setTag(ACTION_PERMISSION_KEY, value.toLowerCase(Locale.ENGLISH));
    }

    public static String getDefaultCooldownId() {
        return DEFAULT_COOLDOWN_ID;
    }

    public static void setVisualCooldown(TagItem item, boolean value) {
        if (value) //default
            item.setTag(ACTION_VISUAL_COOLDOWN, true);
        else
            item.removeTag(ACTION_VISUAL_COOLDOWN);
    }

    public static boolean getVisualCooldown(TagItem item) {
        return item.hasBooleanTag(ACTION_VISUAL_COOLDOWN);
    }

    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        if (args.length == 1) {
            p.openInventory(new ActionsGui(p, item).getInventory());
            //onFail(p, alias);
            return;
        }
        try {
            switch (args[1].toLowerCase(Locale.ENGLISH)) {
                case "add":
                    add(p, args, item);
                    return;
                case "addline":
                    addLine(p, args, item);
                    return;
                case "remove":
                    remove(p, args, item);
                    return;
                case "set":
                    set(p, args, item);
                    return;
                case "uses":
                    uses(p, args, item);
                    return;
                case "maxuses":
                    maxUses(p, args, item);
                    return;
                case "consume":
                    consume(p, args, item);
                    return;
                case "cooldown":
                    cooldown(p, args, item);
                    return;
                case "cooldownid":
                    cooldownId(p, args, item);
                    return;
                case "permission":
                    permission(p, args, item);
                    return;
                case "visualcooldown":
                    visualCooldown(p, args, item);
                    return;
                case "displayuses":
                    displayUses(p, args, item);
                case "info":
                    p.openInventory(new ActionsGui(p, item).getInventory());
                    //info(p, args, item);
                    return;
            }
            onFail(p, alias);
        } catch (Exception e) {
            e.printStackTrace();
            onFail(p, alias);
        }
    }

    //it actions displayUses [boolean]
    private void displayUses(Player p, String[] args, ItemStack item) {
        TagItem tagItem = ItemTag.getTagItem(item);
        boolean value = args.length >= 3 ? Aliases.BOOLEAN.convertAlias(args[2]) : !getDisplayUses(tagItem);
        setDisplayUses(tagItem, value);
        updateUsesDisplay(item);
        if (value) {
            sendLanguageString("displayuses.feedback.set", "", p);
            updateUsesDisplay(item);
        } else {
            sendLanguageString("displayuses.feedback.unset", "", p);
            updateUsesDisplay(item);
        }
    }

    private void updateUsesDisplay(ItemStack item) {
        TagItem tagItem = ItemTag.getTagItem(item);
        boolean show = getDisplayUses(tagItem);
        Map<String, Object> metaMap = new LinkedHashMap<>(item.getItemMeta().serialize());
        /*if (show && !map.containsKey("meta"))
            map.put("meta",new LinkedHashMap<String,Object>());

        if (map.containsKey("meta")) {
            Map<String, Object> metaMap = (Map<String, Object>) map.get("meta");*/
        if (show && !metaMap.containsKey("lore"))
            metaMap.put("lore", new ArrayList<String>());

        if (metaMap.containsKey("lore")) {
            List<String> lore = new ArrayList<>((Collection<String>) metaMap.get("lore"));
            //that's a bit hardcoded!
            lore.removeIf((line) -> (line.startsWith(
                    "{\"italic\":false,\"color\":\"white\",\"translate\":\"item.durability\",\"with\":[{\"text\":\"")
                    && line.endsWith("\"}]}") || line.startsWith(
                    "{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"white\",\"text\":\"Durability:")
                    && line.endsWith("\"}],\"text\":\"\"}")));
            if (show) {
                int uses = getUses(tagItem);
                int maxUses = getMaxUses(tagItem);
                lore.add("{\"italic\":false,\"color\":\"white\",\"translate\":\"item.durability\",\"with\":[{\"text\":\"" +
                        (uses == -1 ? "∞" : uses) + "\"},{\"text\":\"" + (maxUses == -1 ? "∞" : maxUses) + "\"}]}");
            }
            if (!lore.isEmpty())
                metaMap.put("lore", lore);
            else
                metaMap.remove("lore");
        }
        metaMap.put("==", "ItemMeta");
        item.setItemMeta((ItemMeta) ConfigurationSerialization.deserializeObject(metaMap));
    }

    //it actions visualcooldown [boolean]
    private void visualCooldown(Player sender, String[] args, ItemStack item) {
        TagItem tagItem = ItemTag.getTagItem(item);
        boolean value = args.length >= 3 ? Aliases.BOOLEAN.convertAlias(args[2]) : !getVisualCooldown(tagItem);
        setVisualCooldown(tagItem, value);
        if (value)
            sendLanguageString("visualcooldown.feedback.set", "", sender);
        else
            sendLanguageString("visualcooldown.feedback.unset", "", sender);
    }

    //it actions consume [boolean]
    private void consume(Player sender, String[] args, ItemStack item) {
        TagItem tagItem = ItemTag.getTagItem(item);
        boolean value = args.length >= 3 ? Aliases.BOOLEAN.convertAlias(args[2]) : !getConsume(tagItem);
        setConsume(tagItem, value);
        if (value)
            sendLanguageString("consume.feedback.set", "", sender);
        else
            sendLanguageString("consume.feedback.unset", "", sender);
    }

    // itemtag actions setpermission <permission>
    private void permission(Player p, String[] args, ItemStack item) {
        try {
            if (args.length > 3)
                throw new IllegalArgumentException("Wrong param number");
            String permission = args.length == 2 ? null : args[2].toLowerCase(Locale.ENGLISH);
            TagItem tagItem = ItemTag.getTagItem(item);
            setPermission(tagItem, permission);
            if (permission != null)
                sendLanguageString("permission.feedback.set", "", p, "%permission%", permission);
            else
                sendLanguageString("permission.feedback.removed", "", p);
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
            TagItem tagItem = ItemTag.getTagItem(item);
            setCooldownId(tagItem, cooldownId);
            if (cooldownId != null)
                sendLanguageString("cooldownid.feedback.set", "", p, "%id%", cooldownId);
            else
                sendLanguageString("cooldownid.feedback.removed", "", p);

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
            TagItem tagItem = ItemTag.getTagItem(item);
            setCooldownMs(tagItem, cooldownMs);
            if (cooldownMs > 0)
                sendLanguageString("cooldown.feedback.set", "", p, "%cooldown_ms%",
                        String.valueOf(cooldownMs), "%cooldown_seconds%", String.valueOf(cooldownMs / 1000));
            else
                sendLanguageString("cooldown.feedback.removed", "", p);
        } catch (Exception e) {
            Util.sendMessage(p, this.craftFailFeedback(getLanguageString("cooldown.params", null, p),
                    getLanguageStringList("cooldown.description", null, p)));
        }
    }


    private void maxUses(Player p, String[] args, ItemStack item) {
        try {
            if (args.length > 3)
                throw new IllegalArgumentException("Wrong param number");
            int uses = args.length == 2 ? 1 : Integer.parseInt(args[2]);
            if (uses == 0)
                throw new IllegalArgumentException();
            TagItem tagItem = ItemTag.getTagItem(item);
            setMaxUses(tagItem, uses);
            if (getDisplayUses(tagItem))
                updateUsesDisplay(item);
            if (uses < 0)
                sendLanguageString("maxuses.feedback.unlimited", "", p);
            else
                sendLanguageString("maxuses.feedback.set", "", p,
                        "%uses%", String.valueOf(uses));
        } catch (Exception e) {
            Util.sendMessage(p, this.craftFailFeedback(getLanguageString("maxuses.params", null, p),
                    getLanguageStringList("maxuses.description", null, p)));
        }
    }

    private void uses(Player p, String[] args, ItemStack item) {
        try {
            if (args.length > 3)
                throw new IllegalArgumentException("Wrong param number");
            int uses = args.length == 2 ? 1 : Integer.parseInt(args[2]);
            if (uses == 0)
                throw new IllegalArgumentException();
            TagItem tagItem = ItemTag.getTagItem(item);
            setUses(tagItem, uses);
            if (getDisplayUses(tagItem))
                updateUsesDisplay(item);
            if (uses < 0)
                sendLanguageString("uses.feedback.unlimited", "", p);
            else
                sendLanguageString("uses.feedback.set", "", p,
                        "%uses%", String.valueOf(uses));
        } catch (Exception e) {
            Util.sendMessage(p, this.craftFailFeedback(getLanguageString("uses.params", null, p),
                    getLanguageStringList("uses.description", null, p)));
        }
    }

    private void invalidAction(Player p, String actionError) {
        String msg = getLanguageString("invalid-action.message", "", p, "%error%", actionError);
        if (msg == null || msg.isEmpty())
            return;
        StringBuilder hover = new StringBuilder(getLanguageString("invalid-action.hover-pre", "", p)).append("\n");
        String color1 = getLanguageString("invalid-action.first_color", "", p);
        String color2 = getLanguageString("invalid-action.second_color", "", p);
        boolean color = true;
        int counter = 0;
        for (String actionType : ActionHandler.getTypes()) {
            counter += actionType.length() + 1;
            hover.append(color ? color1 : color2).append(actionType);
            color = !color;
            if (counter > 30) {
                counter = 0;
                hover.append("\n");
            } else {
                hover.append(" ");
            }
        }
        Util.sendMessage(p, new ComponentBuilder(msg).event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder(hover.toString())).create())).create());
    }

    private void invalidActionInfo(Player p, String actionType, String actionInfo) {
        String msg = getLanguageString("invalid-actioninfo.message", "", p, "%error%", actionInfo, "%action%", actionType);
        if (msg == null || msg.isEmpty())
            return;
        Util.sendMessage(p, new ComponentBuilder(msg).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(String.join("\n", UtilsString.fix(ActionHandler.getAction(actionType).getInfo(), p, true))).create())).create());
    }

    private void set(Player p, String[] args, ItemStack item) {
        try {
            if (args.length < 4)
                throw new IllegalArgumentException("Wrong param number");
            int line = Integer.parseInt(args[2]) - 1;
            if (line < 0)
                throw new IllegalArgumentException();
            //ArrayList<String> tmp = new ArrayList<>(Arrays.asList(args).subList(4, args.length));
            String actionType = args[3].toLowerCase(Locale.ENGLISH);
            String actionInfo = String.join(" ", Arrays.asList(args).subList(4, args.length));
            String originalAction = String.join(" ", Arrays.asList(args).subList(3, args.length));
            try {
                ActionHandler.validateActionType(actionType);
            } catch (Exception e) {
                invalidAction(p, actionType);
                return;
            }
            try {
                ActionHandler.validateActionInfo(actionType, actionInfo);
            } catch (Exception e) {
                invalidActionInfo(p, actionType, actionInfo);
                return;
            }
            String action = actionType + TYPE_SEPARATOR + actionInfo;
            TagItem tagItem = ItemTag.getTagItem(item);

            if (!hasActions(tagItem))
                setActions(tagItem, Collections.singletonList(action));
            else {
                List<String> list = new ArrayList<>(getActions(tagItem));
                list.set(line, action);
                setActions(tagItem, list);
            }
            sendLanguageString("set.feedback", "", p, "%line%",
                    String.valueOf(line + 1), "%action%", originalAction);
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
            TagItem tagItem = ItemTag.getTagItem(item);
            String action;
            if (!hasActions(tagItem))
                throw new IllegalArgumentException();
            else {
                List<String> list = new ArrayList<>(getActions(tagItem));
                action = list.remove(line);
                setActions(tagItem, list);
            }
            sendLanguageString("remove.feedback", "", p, "%line%",
                    String.valueOf(line + 1), "%action%", action.split(TYPE_SEPARATOR)[0]);
        } catch (Exception e) {
            Util.sendMessage(p, this.craftFailFeedback(getLanguageString("remove.params", null, p),
                    getLanguageStringList("remove.description", null, p)));
        }
    }

    private void add(Player p, String[] args, ItemStack item) {
        try {
            if (args.length < 3)
                throw new IllegalArgumentException("Wrong param number");
            String actionType = args[2].toLowerCase(Locale.ENGLISH);
            String actionInfo = String.join(" ", Arrays.asList(args).subList(3, args.length));
            String originalAction = String.join(" ", Arrays.asList(args).subList(2, args.length));
            try {
                ActionHandler.validateActionType(actionType);
            } catch (Exception e) {
                invalidAction(p, actionType);
                return;
            }
            try {
                ActionHandler.validateActionInfo(actionType, actionInfo);
            } catch (Exception e) {
                invalidActionInfo(p, actionType, actionInfo);
                return;
            }

            actionInfo = ActionHandler.fixActionInfo(actionType, actionInfo);
            String action = actionType + TYPE_SEPARATOR + actionInfo;
            TagItem tagItem = ItemTag.getTagItem(item);
            if (!hasActions(tagItem))
                setActions(tagItem, Collections.singletonList(action));
            else {
                List<String> list = new ArrayList<>(getActions(tagItem));
                list.add(action);
                setActions(tagItem, list);
            }
            sendLanguageString("add.feedback", "", p, "%action%",
                    originalAction);
        } catch (Exception e) {
            e.printStackTrace();
            Util.sendMessage(p, this.craftFailFeedback(getLanguageString("add.params", null, p),
                    getLanguageStringList("add.description", null, p)));
        }
    }

    private void addLine(Player p, String[] args, ItemStack item) {
        try {
            if (args.length < 4)
                throw new IllegalArgumentException("Wrong param number");
            int line = Integer.parseInt(args[2]) - 1;
            String actionType = args[3].toLowerCase(Locale.ENGLISH);
            String actionInfo = String.join(" ", Arrays.asList(args).subList(4, args.length));
            String originalAction = String.join(" ", Arrays.asList(args).subList(3, args.length));
            try {
                ActionHandler.validateActionType(actionType);
            } catch (Exception e) {
                invalidAction(p, actionType);
                return;
            }
            try {
                ActionHandler.validateActionInfo(actionType, actionInfo);
            } catch (Exception e) {
                invalidActionInfo(p, actionType, actionInfo);
                return;
            }
            String action = actionType + TYPE_SEPARATOR + actionInfo;
            TagItem tagItem = ItemTag.getTagItem(item);
            if (!hasActions(tagItem))
                setActions(tagItem, Collections.singletonList(action));
            else {
                List<String> list = new ArrayList<>(getActions(tagItem));
                list.add(line, action);
                setActions(tagItem, list);
            }
            sendLanguageString("addline.feedback", "", p, "%action%",
                    originalAction, "%line%", String.valueOf(line + 1));
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
                switch (args[1].toLowerCase(Locale.ENGLISH)) {
                    case "add":
                        return Util.complete(args[2], ActionHandler.getTypes());
                    case "uses":
                    case "maxuses":
                        return Util.complete(args[2], Arrays.asList("-1", "1", "5", "10"));
                    case "visualcooldown":
                    case "consume":
                    case "displayuses":
                        return Util.complete(args[2], Aliases.BOOLEAN);
                }
                return Collections.emptyList();
            case 4:
                switch (args[1].toLowerCase(Locale.ENGLISH)) {
                    case "add":
                        return ActionHandler.tabComplete(sender, args[2].toLowerCase(Locale.ENGLISH), new ArrayList<>(Arrays.asList(args).subList(3, args.length)));
                    case "set":
                    case "addline":
                        return Util.complete(args[3], ActionHandler.getTypes());
                }
                return Collections.emptyList();
            default:
                switch (args[1].toLowerCase(Locale.ENGLISH)) {
                    case "add":
                        return ActionHandler.tabComplete(sender, args[2].toLowerCase(Locale.ENGLISH), new ArrayList<>(Arrays.asList(args).subList(3, args.length)));
                    case "set":
                    case "addline":
                        return ActionHandler.tabComplete(sender, args[3].toLowerCase(Locale.ENGLISH), new ArrayList<>(Arrays.asList(args).subList(4, args.length)));
                }
        }
        return Collections.emptyList();
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    private void event(PlayerInteractEvent event) {
        switch (event.getAction()) {
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                ItemStack item = event.getItem();
                TagItem tagItem = ItemTag.getTagItem(item);
                if (!hasActions(tagItem))
                    return;

                String permission = getPermission(tagItem);
                if (permission != null && !event.getPlayer().hasPermission(permission))
                    return;
                long cooldown = getCooldownMs(tagItem);
                if (cooldown > 0) {
                    String cooldownId = getCooldownId(tagItem);
                    if (ItemTag.get().getCooldownAPI().hasCooldown(event.getPlayer(), cooldownId))
                        return;
                    ItemTag.get().getCooldownAPI().setCooldown(event.getPlayer(), cooldownId, cooldown);
                    if (Actions.getVisualCooldown(tagItem))
                        event.getPlayer().setCooldown(item.getType(), (int) (cooldown / 50));
                }

                int uses = getUses(tagItem);
                if (uses == 0)
                    return;

                for (String action : Actions.getActions(tagItem))
                    try {
                        if (action.isEmpty())
                            continue;
                        ActionHandler.handleAction(event.getPlayer(), action.split(TYPE_SEPARATOR)[0],
                                action.split(TYPE_SEPARATOR)[1]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                event.setCancelled(true);
                if (uses > 0) {
                    if (uses == 1 && getConsume(tagItem)) {
                        if (event.getItem().getAmount() == 1) { //1.8 doesn't like  event.getItem().setAmount(event.getItem().getAmount() - 1); on single items
                            try {
                                if (event.getHand() == EquipmentSlot.HAND)
                                    event.getPlayer().getInventory().setItemInMainHand(null);
                                else
                                    event.getPlayer().getInventory().setItemInOffHand(null);
                            } catch (Error e) {
                                event.getPlayer().getInventory().setItemInHand(null);
                            }
                        } else {
                            ItemStack clone = event.getItem().clone();
                            clone.setAmount(clone.getAmount() - 1);
                            try {
                                if (event.getHand() == EquipmentSlot.HAND)
                                    event.getPlayer().getInventory().setItemInMainHand(clone);
                                else
                                    event.getPlayer().getInventory().setItemInOffHand(clone);
                            } catch (Error e) {
                                event.getPlayer().getInventory().setItemInHand(clone);
                            }
                        }
                    } else {
                        try {
                            if (event.getItem().getAmount() == 1) {
                                setUses(tagItem, uses - 1);
                                if (getDisplayUses(tagItem))
                                    updateUsesDisplay(item);
                            } else {
                                ItemStack clone = event.getItem();
                                clone.setAmount(clone.getAmount() - 1);
                                if (event.getHand() == EquipmentSlot.HAND)
                                    event.getPlayer().getInventory().setItemInMainHand(clone);
                                else
                                    event.getPlayer().getInventory().setItemInOffHand(clone);
                                setUses(ItemTag.getTagItem(clone), uses - 1);
                                if (getDisplayUses(tagItem))
                                    updateUsesDisplay(clone);
                                UtilsInventory.giveAmount(event.getPlayer(), clone, 1, ExcessManage.DROP_EXCESS);
                            }

                        } catch (Throwable t) { //1.8 compability
                            if (event.getItem().getAmount() == 1)
                                tagItem.setTag(ACTION_USES_KEY, uses - 1);
                            else {
                                ItemStack clone = event.getItem();
                                clone.setAmount(clone.getAmount() - 1);
                                event.getPlayer().getInventory().setItemInHand(clone);
                                ItemTag.getTagItem(clone).setTag(ACTION_USES_KEY, uses - 1);
                                //has no displayuses on 1.8
                                UtilsInventory.giveAmount(event.getPlayer(), clone, 1, ExcessManage.DROP_EXCESS);
                            }
                        }
                    }
                }
            default:
        }
    }

    private void sendClickableText(Gui gui, String postClickable) {
        Util.sendMessage(gui.getTargetPlayer(), new ComponentBuilder(gui.getLanguageMessage("gui.actions.click-interact")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(gui.getLanguageMessage("gui.actions.click-hover")).create()))
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/it " + Actions.this.getName() + " " + postClickable + " ")).create());
    }

    private class ActionsGui implements Gui {
        private final TagItem tagItem;
        private final Inventory inventory;
        private final Player target;
        private int editorCooldown = 1;
        private int editorValue = 1;

        public ActionsGui(Player target, ItemStack item) {
            String title = this.getLanguageMessage("gui.actions.title",
                    "%player_name%", target.getName());
            this.inventory = Bukkit.createInventory(this, 2 * 9, title);
            this.target = target;
            tagItem = ItemTag.getTagItem(item);

            updateInventory();
        }

        @Override
        public void onClose(InventoryCloseEvent event) {
        }

        @Override
        public void onClick(InventoryClickEvent event) {
            if (!event.getWhoClicked().equals(target))
                return;
            if (!inventory.equals(event.getClickedInventory()))
                return;
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)
                return;
            switch (event.getSlot()) {
                case 0:
                case 1:
                case 9: {
                    getTargetPlayer().openInventory(new ActionTypeGui().getInventory());
                    return;
                }
                case 8: {
                    //Set uses
                    switch (event.getClick()) {
                        case LEFT:
                            setUses(this.tagItem, Math.max(-1, Actions.getUses(tagItem) - editorValue));
                            if (getDisplayUses(tagItem))
                                updateUsesDisplay(tagItem.getItem());
                            break;
                        case SHIFT_RIGHT:
                            editorValue = Math.min(1000000, editorValue * 10);
                            break;
                        case RIGHT:
                            setUses(this.tagItem, (int) Math.min(Integer.MAX_VALUE, Actions.getUses(tagItem) + (long) editorValue));
                            if (getDisplayUses(tagItem))
                                updateUsesDisplay(tagItem.getItem());
                            break;
                        case SHIFT_LEFT:
                            editorValue = Math.max(1, editorValue / 10);
                            break;
                        default:
                            return;
                    }
                    updateInventory();
                    return;
                }
                case 17: {
                    //Set maxuses
                    switch (event.getClick()) {
                        case LEFT:
                            setMaxUses(this.tagItem, Math.max(-1, Actions.getMaxUses(tagItem) - editorValue));
                            if (getDisplayUses(this.tagItem))
                                updateUsesDisplay(tagItem.getItem());
                            break;
                        case SHIFT_RIGHT:
                            editorValue = Math.min(1000000, editorValue * 10);
                            break;
                        case RIGHT:
                            setMaxUses(this.tagItem, (int) Math.min(Integer.MAX_VALUE, Actions.getMaxUses(tagItem) + (long) editorValue));
                            if (getDisplayUses(this.tagItem))
                                updateUsesDisplay(tagItem.getItem());
                            break;
                        case SHIFT_LEFT:
                            editorValue = Math.max(1, editorValue / 10);
                            break;
                        default:
                            return;
                    }
                    updateInventory();
                    return;
                }
                case 7: {
                    //consume on 0
                    setConsume(tagItem, !getConsume(tagItem));
                    updateInventory();
                    return;
                }
                case 5: {//permission
                    if (event.getClick() == ClickType.SHIFT_RIGHT) {
                        Actions.setPermission(tagItem, null);
                        updateInventory();
                        return;
                    }
                    getTargetPlayer().closeInventory();
                    sendClickableText(this, "permission");
                    return;
                }
                case 6: {//cooldown
                    switch (event.getClick()) {
                        case LEFT:
                            Actions.setCooldownMs(this.tagItem,
                                    Math.max(0, Actions.getCooldownMs(tagItem)
                                            - editorCooldown * 1000));
                            break;
                        case SHIFT_RIGHT:
                            editorCooldown = Math.min(1000000, editorCooldown * 10);
                            break;
                        case RIGHT:
                            Actions.setCooldownMs(this.tagItem,
                                    (int) Math.min(Integer.MAX_VALUE, Actions.getCooldownMs(tagItem)
                                            + editorCooldown * 1000L));
                            break;
                        case SHIFT_LEFT:
                            editorCooldown = Math.max(1, editorCooldown / 10);
                            break;
                        default:
                            return;
                    }
                    updateInventory();
                    return;
                }
                case 16: {//cooldownid
                    if (event.getClick() == ClickType.SHIFT_RIGHT) {
                        Actions.setCooldownId(tagItem, null);
                        updateInventory();
                        return;
                    }
                    getTargetPlayer().closeInventory();
                    sendClickableText(this, "cooldownid");
                    return;
                }
                case 15: {//cooldown display
                    Actions.setVisualCooldown(tagItem, !Actions.getVisualCooldown(tagItem));
                    updateInventory();
                    return;
                }
                case 14: {//display uses
                    Actions.setDisplayUses(tagItem, !Actions.getDisplayUses(tagItem));
                    if (getDisplayUses(this.tagItem))
                        updateUsesDisplay(tagItem.getItem());
                    updateInventory();
                    return;
                }
            }
        }

        @Override
        public void onDrag(InventoryDragEvent event) {
        }

        @Override
        public void onOpen(InventoryOpenEvent event) {
            updateInventory();
        }

        @Override
        public @NotNull Inventory getInventory() {
            return inventory;
        }

        @Override
        public Player getTargetPlayer() {
            return target;
        }

        @Override
        public @NotNull ItemTag getPlugin() {
            return ItemTag.get();
        }

        private void updateInventory() {
            ItemStack item;
            ItemMeta meta;
            //add addline lime
            try {
                item = this.loadLanguageDescription(this.getGuiItem("gui.actions.addline", Material.LIME_DYE),
                        "gui.actions.addline");
            } catch (Throwable t) {
                item = Util.getDyeItemFromColor(DyeColor.LIME);
                item = this.loadLanguageDescription(this.getGuiItem("gui.actions.addline", item.getType(),
                        item.getDurability()), "gui.actions.addline");
            }
            this.getInventory().setItem(0, item);
            //set blue
            try {
                item = this.loadLanguageDescription(this.getGuiItem("gui.actions.set", Material.BLUE_DYE), "gui.actions.set");
            } catch (Throwable t) {
                item = Util.getDyeItemFromColor(DyeColor.BLUE);
                item = this.loadLanguageDescription(this.getGuiItem("gui.actions.set", item.getType(), item.getDurability()), "gui.actions.set");
            }
            this.getInventory().setItem(9, item);

            //remove red
            try {
                item = this.loadLanguageDescription(this.getGuiItem("gui.actions.remove", Material.RED_DYE), "gui.actions.remove");
            } catch (Throwable t) {
                item = Util.getDyeItemFromColor(DyeColor.RED);
                item = this.loadLanguageDescription(this.getGuiItem("gui.actions.remove", item.getType(), item.getDurability()), "gui.actions.remove");
            }
            this.getInventory().setItem(1, item);

            //consume on last use
            item = this.getGuiItem("gui.actions.consume", Material.APPLE);
            meta = this.loadLanguageDescription(item.getItemMeta(), "gui.actions.consume",
                    "%value%", Aliases.BOOLEAN.getName(getConsume(tagItem)));
            if (!getConsume(tagItem))
                meta.addEnchant(Enchantment.LURE, 1, true);
            else
                meta.removeEnchant(Enchantment.LURE);
            item.setItemMeta(meta);
            this.getInventory().setItem(7, item);

            // uses
            this.getInventory().setItem(8, this.loadLanguageDescription(this.getGuiItem("gui.actions.uses", Material.IRON_PICKAXE), "gui.actions.uses",
                    "%value%", getUses(tagItem) == -1 ? "-1 (Unlimited)" : String.valueOf(getUses(tagItem)), "%editor%", String.valueOf(editorValue)
                    , "%editor-prev%", String.valueOf(Math.max(1, editorValue / 10)), "%editor-next%", String.valueOf(Math.min(1000000, editorValue * 10))));

            if (Util.isVersionAfter(1, 9)) {
                //max uses
                this.getInventory().setItem(17, this.loadLanguageDescription(this.getGuiItem("gui.actions.maxuses", Material.DIAMOND_PICKAXE), "gui.actions.maxuses",
                        "%value%", getMaxUses(tagItem) == -1 ? "-1 (Unlimited)" : String.valueOf(getMaxUses(tagItem)), "%editor%", String.valueOf(editorValue)
                        , "%editor-prev%", String.valueOf(Math.max(1, editorValue / 10)), "%editor-next%", String.valueOf(Math.min(1000000, editorValue * 10))));


                item = this.getGuiItem("gui.actions.displayuses", Material.PAINTING);
                meta = this.loadLanguageDescription(item.getItemMeta(), "gui.actions.displayuses", "%value%", Aliases.BOOLEAN.getName(getDisplayUses(tagItem)));
                if (getDisplayUses(tagItem))
                    meta.addEnchant(Enchantment.LURE, 1, true);
                else
                    meta.removeEnchant(Enchantment.LURE);
                item.setItemMeta(meta);
                //max uses
                this.getInventory().setItem(14, item);
            }
            //permission
            try {
                this.getInventory().setItem(5, this.loadLanguageDescription(this.getGuiItem("gui.actions.permission", Material.IRON_BARS), "gui.actions.permission",
                        "%value%", getPermission(tagItem) == null ? "<none>" : getPermission(tagItem)));
            } catch (Error e) {
                this.getInventory().setItem(5, this.loadLanguageDescription(this.getGuiItem("gui.actions.permission", Material.valueOf("IRON_FENCE")), "gui.actions.permission",
                        "%value%", getPermission(tagItem) == null ? "<none>" : getPermission(tagItem)));
            }

            // cooldown
            this.getInventory().setItem(6, this.loadLanguageDescription(this.getGuiItem("gui.actions.cooldown", Material.COMPASS), "gui.actions.cooldown",
                    "%value_s%", String.valueOf(getCooldownMs(tagItem) / 1000), "%editor%", String.valueOf(editorCooldown)
                    , "%editor-prev%", String.valueOf(Math.max(1, editorCooldown / 10)), "%editor-next%", String.valueOf(Math.min(1000000, editorCooldown * 10))));

            //cooldownid
            this.getInventory().setItem(16, this.loadLanguageDescription(this.getGuiItem("gui.actions.cooldownid", Material.NAME_TAG), "gui.actions.cooldownid",
                    "%value%", getCooldownId(tagItem)));

            //visualcooldown
            item = this.getGuiItem("gui.actions.visualcooldown", Material.ENDER_PEARL);
            meta = this.loadLanguageDescription(item.getItemMeta(), "gui.actions.visualcooldown", "%value%", Aliases.BOOLEAN.getName(getVisualCooldown(tagItem)));
            if (getVisualCooldown(tagItem))
                meta.addEnchant(Enchantment.LURE, 1, true);
            else
                meta.removeEnchant(Enchantment.LURE);
            item.setItemMeta(meta);
            this.getInventory().setItem(15, item);

            //info
            item = this.getGuiItem("gui.actions.info", Material.PAPER);
            meta = this.loadLanguageDescription(item.getItemMeta(), "gui.actions.info");
            List<String> lore = new ArrayList<>(meta.hasLore() ? meta.getLore() : Collections.emptyList());
            List<String> list = Actions.getActions(tagItem);
            if (list != null)
                for (String action : list)
                    if (action != null && !action.isEmpty())
                        lore.add(ChatColor.YELLOW + action.replace(Actions.TYPE_SEPARATOR, " " + ChatColor.WHITE));
            meta.setLore(lore);
            item.setItemMeta(meta);
            this.getInventory().setItem(2, item);
        }

        private class ActionTypeGui implements PagedGui {

            private static final int ROWS = 5;
            private final Inventory inventory;
            private final int page;
            private final int maxPages;

            public ActionTypeGui() {
                this(1);
            }

            public ActionTypeGui(int page) {
                if (page < 1)
                    throw new NullPointerException();
                String title = this.getLanguageMessage("gui.actions.title",
                        "%player_name%", target.getName());
                this.inventory = Bukkit.createInventory(this, (ROWS + 1) * 9, title);
                updateInventory();
                List<String> actionsList = getActions(tagItem);
                int actions = actionsList == null ? 0 : actionsList.size() + 1;
                int maxPages = (actions) / (ROWS * 9) + ((actions) % (ROWS * 9) == 0 ? 0 : 1);
                if (page > maxPages)
                    page = maxPages;
                this.maxPages = maxPages;
                this.page = page;
            }

            /**
             * @return 1+
             */
            public int getPage() {
                return this.page;
            }

            @Override
            public void onClose(InventoryCloseEvent event) {
            }

            @Override
            public void onClick(InventoryClickEvent event) {
                if (!event.getWhoClicked().equals(target))
                    return;
                if (!inventory.equals(event.getClickedInventory()))
                    return;
                if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)
                    return;
                if (event.getSlot() > inventory.getSize() - 9) {
                    switch (inventory.getSize() - event.getSlot()) {
                        case 2:
                            target.openInventory(new ActionTypeGui(page + 1).getInventory());
                            return;
                        case 5:
                            getTargetPlayer().openInventory(ActionsGui.this.getInventory());
                            return;
                        default:
                            target.openInventory(new ActionTypeGui(page - 1).getInventory());
                            return;
                    }
                }
                List<String> actions = getActions(tagItem);
                if (actions == null)
                    actions = Collections.emptyList();
                if (page > 1)//based on page
                    if ((page - 1) * ROWS * 9 > actions.size())
                        actions = Collections.emptyList();
                    else
                        actions = actions.subList((page - 1) * ROWS * 9, actions.size());
                if (actions.size() > event.getSlot()) {
                    switch (event.getClick()) {
                        case RIGHT:
                            getTargetPlayer().closeInventory();
                            sendClickableText(this, "set "
                                    + (event.getSlot() + 1) + " " + actions.get(event.getSlot()).replace(TYPE_SEPARATOR, " "));
                            return;
                        case LEFT:
                            getTargetPlayer().closeInventory();
                            sendClickableText(this, "addline " + (event.getSlot() + 1));
                            return;
                        case SHIFT_RIGHT:
                            actions = new ArrayList<>(actions);
                            actions.remove(event.getSlot());
                            setActions(tagItem, actions);
                            updateInventory();
                    }
                    return;
                }
                if (actions.size() == event.getSlot() && event.getClick() == ClickType.LEFT) {
                    getTargetPlayer().closeInventory();
                    sendClickableText(this, "add");
                }
            }

            @Override
            public void onDrag(InventoryDragEvent event) {
            }

            @Override
            public void onOpen(InventoryOpenEvent event) {
                updateInventory();
            }

            private void updateInventory() {
                List<String> actions = getActions(tagItem);
                if (actions != null) {
                    if (page > 1)
                        if ((page - 1) * ROWS * 9 > actions.size())
                            actions = Collections.emptyList();
                        else
                            actions = actions.subList((page - 1) * ROWS * 9, actions.size());
                    for (int i = 0; i < actions.size(); i++) {
                        if (i >= 45)
                            break;
                        int index = actions.get(i).indexOf(TYPE_SEPARATOR);
                        String actionPre = actions.get(i).substring(0, index);
                        String actionPost = actions.get(i).substring(index + TYPE_SEPARATOR.length());
                        ItemStack item;
                        try {
                            item = this.getGuiItem("gui.actionslines.line", Material.COMMAND_BLOCK);
                        } catch (Error e) {
                            item = this.getGuiItem("gui.actionslines.line", Material.valueOf("COMMAND"));
                        }
                        ItemMeta meta = item.getItemMeta();
                        String action = this.getPlugin().getLanguageConfig(target).getMessage("gui.actionslines.actionformat"
                                , "", "%type%", actionPre, "%info%", actionPost);
                        this.loadLanguageDescription(meta, "gui.actionslines.element", "%action%", action);
                        item.setAmount(i + 1);
                        item.setItemMeta(meta);
                        this.inventory.setItem(i, item);
                    }
                }
                if (actions == null || actions.size() < 45) {
                    ItemStack item;
                    try {
                        item = this.getGuiItem("gui.actionslines.line", Material.COMMAND_BLOCK);
                    } catch (Error e) {
                        item = this.getGuiItem("gui.actionslines.line", Material.valueOf("COMMAND"));
                    }
                    ItemMeta meta = item.getItemMeta();
                    this.loadLanguageDescription(meta, "gui.actionslines.add");
                    item.setItemMeta(meta);
                    item.setAmount(actions == null ? 1 : actions.size() + 1);
                    this.inventory.setItem(actions == null ? 0 : actions.size(), item);
                    this.inventory.setItem(actions == null ? 1 : actions.size() + 1, null);
                }
                this.inventory.setItem(49, this.getBackItem());
                //here arrows
                if (page > 1)
                    this.inventory.setItem(ROWS * 9 + 1, getPreviousPageItem());
                if (page < maxPages)
                    this.inventory.setItem(ROWS * 9 + 7, getNextPageItem());
            }

            @Override
            public @NotNull Inventory getInventory() {
                return inventory;
            }

            @Override
            public Player getTargetPlayer() {
                return target;
            }

            @Override
            public @NotNull ItemTag getPlugin() {
                return ItemTag.get();
            }
        }
    }


}

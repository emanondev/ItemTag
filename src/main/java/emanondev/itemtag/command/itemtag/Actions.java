package emanondev.itemtag.command.itemtag;

import emanondev.itemedit.Util;
import emanondev.itemedit.UtilsInventory;
import emanondev.itemedit.UtilsInventory.ExcessManage;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.gui.Gui;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.TagItem;
import emanondev.itemtag.actions.Action;
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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Actions extends ListenerSubCmd {

    private final static String ACTIONS_KEY = ItemTag.get().getName().toLowerCase() + ":actions";
    private final static String ACTION_USES_KEY = ItemTag.get().getName().toLowerCase() + ":uses";
    private final static String ACTION_CONSUME_AT_END_KEY = ItemTag.get().getName().toLowerCase() + ":consume";
    private final static String ACTION_VISUAL_COOLDOWN = ItemTag.get().getName().toLowerCase() + ":visualcooldown";
    private final static String ACTION_COOLDOWN_KEY = ItemTag.get().getName().toLowerCase() + ":cooldown";
    private final static String ACTION_COOLDOWN_ID_KEY = ItemTag.get().getName().toLowerCase() + ":cooldown_id";
    private final static String ACTION_PERMISSION_KEY = ItemTag.get().getName().toLowerCase() + ":permission";
    private final static String TYPE_SEPARATOR = "%%:%%";

    public static boolean hasActions(TagItem item) {
        return item.hasStringListTag(ACTIONS_KEY);
    }

    public static List<String> getActions(TagItem item) {
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
        return item.hasStringTag(ACTION_COOLDOWN_ID_KEY) ? item.getString(ACTION_COOLDOWN_ID_KEY) : "default";
    }

    public static void setCooldownId(TagItem item, String value) {
        if (value == null || value.isEmpty() || value.equalsIgnoreCase("default")) // default
            item.removeTag(ACTION_COOLDOWN_ID_KEY);
        else
            item.setTag(ACTION_COOLDOWN_ID_KEY, value.toLowerCase());
    }

    public static String getPermission(TagItem item) {
        return item.hasStringTag(ACTION_PERMISSION_KEY) ? item.getString(ACTION_PERMISSION_KEY) : null;
    }

    public static void setPermission(TagItem item, String value) {
        if (value == null || value.isEmpty()) // default
            item.removeTag(ACTION_PERMISSION_KEY);
        else
            item.setTag(ACTION_PERMISSION_KEY, value.toLowerCase());
    }

    private static final String[] actionsSub = new String[]{"add", "addline", "set", "permission", "cooldown",
            "cooldownid", "uses", "remove", "info", "consume", "visualcooldown"};

    public Actions(ItemTagCommand cmd) {
        super("actions", cmd, true, true);
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
            switch (args[1].toLowerCase()) {
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

    public static void setVisualCooldown(TagItem item, boolean value) {
        if (value) //default
            item.setTag(ACTION_VISUAL_COOLDOWN, true);
        else
            item.removeTag(ACTION_VISUAL_COOLDOWN);
    }

    public static boolean getVisualCooldown(TagItem item) {
        return item.hasBooleanTag(ACTION_VISUAL_COOLDOWN);
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
            String permission = args.length == 2 ? null : args[2].toLowerCase();
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
            String cooldownId = args.length == 2 ? null : args[2].toLowerCase();
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


    private void uses(Player p, String[] args, ItemStack item) {
        try {
            if (args.length > 3)
                throw new IllegalArgumentException("Wrong param number");
            int uses = args.length == 2 ? 1 : Integer.parseInt(args[2]);
            if (uses == 0)
                throw new IllegalArgumentException();
            TagItem tagItem = ItemTag.getTagItem(item);
            setUses(tagItem, uses);
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
        Action action = ActionHandler.getAction(actionType);
        String msg = getLanguageString("invalid-actioninfo.message", "", p, "%error%", actionInfo, "%action%", actionType);
        if (msg == null || msg.isEmpty())
            return;
        Util.sendMessage(p, new ComponentBuilder(msg).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(String.join("\n", ActionHandler.getAction(actionType).getInfo())).create())).create());
    }

    //
    private void set(Player p, String[] args, ItemStack item) {
        try {
            if (args.length < 4)
                throw new IllegalArgumentException("Wrong param number");
            int line = Integer.parseInt(args[2]) - 1;
            if (line < 0)
                throw new IllegalArgumentException();
            //ArrayList<String> tmp = new ArrayList<>(Arrays.asList(args).subList(4, args.length));
            String actionType = args[3].toLowerCase();
            String actionInfo = String.join(" ", Arrays.asList(args).subList(4, args.length));
            try {
                ActionHandler.validateActionType(actionType);
            } catch (Exception e) {
                invalidAction(p, actionType);
                return;
            }
            try {
                ActionHandler.validateActionInfo(actionType, actionInfo);
            } catch (Exception e) {
                invalidActionInfo(p,actionType,actionInfo);
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
            String actionType = args[2].toLowerCase();
            String actionInfo = String.join(" ", Arrays.asList(args).subList(3, args.length));
            try {
                ActionHandler.validateActionType(actionType);
            } catch (Exception e) {
                invalidAction(p, actionType);
                return;
            }
            try {
                ActionHandler.validateActionInfo(actionType, actionInfo);
            } catch (Exception e) {
                invalidActionInfo(p,actionType,actionInfo);
                return;
            }
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
            String actionType = args[3].toLowerCase();
            String actionInfo = String.join(" ", Arrays.asList(args).subList(4, args.length));
            try {
                ActionHandler.validateActionType(actionType);
            } catch (Exception e) {
                invalidAction(p, actionType);
                return;
            }
            try {
                ActionHandler.validateActionInfo(actionType, actionInfo);
            } catch (Exception e) {
                invalidActionInfo(p,actionType,actionInfo);
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
                    action.replace(TYPE_SEPARATOR, " "), "%line%", String.valueOf(line + 1));
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
                switch (args[1].toLowerCase()) {
                    case "add":
                        return Util.complete(args[2], ActionHandler.getTypes());
                    case "setuses":
                        return Util.complete(args[2], Arrays.asList("-1", "1", "5", "10"));
                    case "visualcooldown":
                    case "consume":
                        return Util.complete(args[2], Aliases.BOOLEAN);
                }
                return Collections.emptyList();
            case 4:
                switch (args[1].toLowerCase()) {
                    case "add":
                        return ActionHandler.tabComplete(sender, args[2].toLowerCase(), new ArrayList<>(Arrays.asList(args).subList(3, args.length)));
                    case "set":
                    case "addline":
                        return Util.complete(args[3], ActionHandler.getTypes());
                }
                return Collections.emptyList();
            default:
                switch (args[1].toLowerCase()) {
                    case "add":
                        return ActionHandler.tabComplete(sender, args[2].toLowerCase(), new ArrayList<>(Arrays.asList(args).subList(3, args.length)));
                    case "set":
                    case "addline":
                        return ActionHandler.tabComplete(sender, args[3].toLowerCase(), new ArrayList<>(Arrays.asList(args).subList(4, args.length)));
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
                if (uses > 0) {
                    if (uses == 1 && getConsume(tagItem))
                        event.getItem().setAmount(event.getItem().getAmount() - 1);
                    else {
                        try {
                            if (event.getItem().getAmount() == 1) {
                                setUses(tagItem, uses - 1);
                            } else {
                                ItemStack clone = event.getItem();
                                clone.setAmount(clone.getAmount() - 1);
                                if (event.getHand() == EquipmentSlot.HAND)
                                    event.getPlayer().getInventory().setItemInMainHand(clone);
                                else
                                    event.getPlayer().getInventory().setItemInOffHand(clone);
                                setUses(ItemTag.getTagItem(clone), uses - 1);
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
                            break;
                        case SHIFT_RIGHT:
                            editorValue = Math.min(1000000, editorValue * 10);
                            break;
                        case RIGHT:
                            setUses(this.tagItem, (int) Math.min(Integer.MAX_VALUE, Actions.getUses(tagItem) + (long) editorValue));
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
                    //consume on 0
                    setConsume(tagItem, !getConsume(tagItem));
                    updateInventory();
                    return;
                }
                case 6: {//permission
                    if (event.getClick() == ClickType.SHIFT_RIGHT) {
                        Actions.setPermission(tagItem, null);
                        updateInventory();
                        return;
                    }
                    getTargetPlayer().closeInventory();
                    sendClickableText(this, "permission");
                    return;
                }
                case 7: {//cooldown
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
                meta.addEnchant(Enchantment.DURABILITY, 1, true);
            else
                meta.removeEnchant(Enchantment.DURABILITY);
            item.setItemMeta(meta);
            this.getInventory().setItem(17, item);

            //consume uses
            this.getInventory().setItem(8, this.loadLanguageDescription(this.getGuiItem("gui.actions.uses", Material.IRON_PICKAXE), "gui.actions.uses",
                    "%value%", getUses(tagItem) == -1 ? "-1 (Unlimited)" : String.valueOf(getUses(tagItem)), "%editor%", String.valueOf(editorValue)
                    , "%editor-prev%", String.valueOf(Math.max(1, editorValue / 10)), "%editor-next%", String.valueOf(Math.min(1000000, editorValue * 10))));

            //permission
            this.getInventory().setItem(6, this.loadLanguageDescription(this.getGuiItem("gui.actions.permission", Material.IRON_BARS), "gui.actions.permission",
                    "%value%", getPermission(tagItem) == null ? "<none>" : getPermission(tagItem)));


            // cooldown
            this.getInventory().setItem(7, this.loadLanguageDescription(this.getGuiItem("gui.actions.cooldown", Material.COMPASS), "gui.actions.cooldown",
                    "%value_s%", String.valueOf(getCooldownMs(tagItem) / 1000), "%editor%", String.valueOf(editorCooldown)
                    , "%editor-prev%", String.valueOf(Math.max(1, editorCooldown / 10)), "%editor-next%", String.valueOf(Math.min(1000000, editorCooldown * 10))));

            //cooldownid
            this.getInventory().setItem(16, this.loadLanguageDescription(this.getGuiItem("gui.actions.cooldownid", Material.NAME_TAG), "gui.actions.cooldownid",
                    "%value%", getCooldownId(tagItem)));

            //visualcooldown
            item = this.getGuiItem("gui.actions.visualcooldown", Material.ENDER_PEARL);
            meta = this.loadLanguageDescription(item.getItemMeta(), "gui.actions.visualcooldown", "%value%", Aliases.BOOLEAN.getName(getVisualCooldown(tagItem)));
            if (getVisualCooldown(tagItem))
                meta.addEnchant(Enchantment.DURABILITY, 1, true);
            else
                meta.removeEnchant(Enchantment.DURABILITY);
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
            this.getInventory().setItem(4, item);
        }

        private int editorCooldown = 1;

        private int editorValue = 1;

        private class ActionTypeGui implements Gui {

            private final Inventory inventory;

            public ActionTypeGui() {
                String title = this.getLanguageMessage("gui.actions.title",
                        "%player_name%", target.getName());
                this.inventory = Bukkit.createInventory(this, 6 * 9, title);
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
                if (event.getSlot() == 49) {
                    getTargetPlayer().openInventory(ActionsGui.this.getInventory());
                    return;
                }
                List<String> actions = getActions(tagItem);
                if (actions == null)
                    actions = Collections.emptyList();
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
                if (actions != null)
                    for (int i = 0; i < actions.size(); i++) {
                        if (i >= 45)
                            break;
                        int index = actions.get(i).indexOf(TYPE_SEPARATOR);
                        String actionPre = actions.get(i).substring(0, index);
                        String actionPost = actions.get(i).substring(index + TYPE_SEPARATOR.length());
                        ItemStack item = this.getGuiItem("gui.actionslines.line", Material.COMMAND_BLOCK);
                        ItemMeta meta = item.getItemMeta();
                        String action = this.getPlugin().getLanguageConfig(target).getMessage("gui.actionslines.actionformat"
                                , "", "%type%", actionPre, "%info%", actionPost);
                        this.loadLanguageDescription(meta, "gui.actionslines.element", "%action%", action);
                        item.setAmount(i + 1);
                        item.setItemMeta(meta);
                        this.inventory.setItem(i, item);
                    }
                if (actions == null || actions.size() < 45) {
                    ItemStack item = this.getGuiItem("gui.actionslines.line", Material.COMMAND_BLOCK);
                    ItemMeta meta = item.getItemMeta();
                    this.loadLanguageDescription(meta, "gui.actionslines.add");
                    item.setItemMeta(meta);
                    item.setAmount(actions == null ? 1 : actions.size() + 1);
                    this.inventory.setItem(actions == null ? 0 : actions.size(), item);
                    this.inventory.setItem(actions == null ? 1 : actions.size() + 1, null);
                }
                this.inventory.setItem(49, this.getBackItem());
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

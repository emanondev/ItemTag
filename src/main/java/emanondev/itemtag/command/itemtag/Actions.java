package emanondev.itemtag.command.itemtag;

import emanondev.itemedit.Util;
import emanondev.itemedit.UtilsString;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.InventoryUtils;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.TagItem;
import emanondev.itemtag.actions.ActionHandler;
import emanondev.itemtag.actions.ActionsUtility;
import emanondev.itemtag.command.ItemTagCommand;
import emanondev.itemtag.command.ListenerSubCmd;
import emanondev.itemtag.gui.ActionsGui;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Actions extends ListenerSubCmd {

    private static final String[] actionsSub = new String[]{"add", "addline", "set", "permission", "cooldown", "cooldownmsg", "cooldownmsgtype",
            "cooldownid", "uses", "maxuses", "remove", "info", "consume", "visualcooldown", "displayuses"};

    public Actions(ItemTagCommand cmd) {
        super("actions", cmd, true, true);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInMainHand(p);
        if (args.length == 1) {
            p.openInventory(new ActionsGui(p, item, alias, this.getName()).getInventory());
            //onFail(p, alias);
            return;
        }
        switch (args[1].toLowerCase(Locale.ENGLISH)) {
            case "add":
                add(p, alias, args, item);
                return;
            case "addline":
                addLine(p, alias, args, item);
                return;
            case "remove":
                remove(p, alias, args, item);
                return;
            case "set":
                set(p, alias, args, item);
                return;
            case "uses":
                uses(p, alias, args, item);
                return;
            case "maxuses":
                maxUses(p, alias, args, item);
                return;
            case "consume":
                consume(p, alias, args, item);
                return;
            case "cooldown":
                cooldown(p, alias, args, item);
                return;
            case "cooldownmsg":
                cooldownMsg(p, alias, args, item);
                return;
            case "cooldownmsgtype":
                cooldownMsgType(p, alias, args, item);
                return;
            case "cooldownid":
                cooldownId(p, alias, args, item);
                return;
            case "permission":
                permission(p, alias, args, item);
                return;
            case "visualcooldown":
                visualCooldown(p, alias, args, item);
                return;
            case "displayuses":
                displayUses(p, alias, args, item);
                return;
            case "info":
                p.openInventory(new ActionsGui(p, item, alias, this.getName()).getInventory());
                //info(p, args, item);
                return;
            default:
                onFail(p, alias);

        }
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return switch (args.length) {
            case 2 -> CompleteUtility.complete(args[1], actionsSub);
            case 3 -> switch (args[1].toLowerCase(Locale.ENGLISH)) {
                case "add" -> CompleteUtility.complete(args[2], ActionHandler.getTypes());
                case "uses", "maxuses" -> CompleteUtility.complete(args[2], Arrays.asList("-1", "1", "5", "10"));
                case "visualcooldown", "consume", "displayuses" -> CompleteUtility.complete(args[2], Aliases.BOOLEAN);
                case "cooldownmsgtype" -> CompleteUtility.complete(args[2], Arrays.asList("chat", "actionbar"));
                default -> List.of();
            };
            case 4 -> switch (args[1].toLowerCase(Locale.ENGLISH)) {
                case "add" ->
                        ActionHandler.tabComplete(sender, args[2].toLowerCase(Locale.ENGLISH), new ArrayList<>(Arrays.asList(args).subList(3, args.length)));
                case "set", "addline" -> CompleteUtility.complete(args[3], ActionHandler.getTypes());
                default -> List.of();
            };
            default -> switch (args[1].toLowerCase(Locale.ENGLISH)) {
                case "add" ->
                        ActionHandler.tabComplete(sender, args[2].toLowerCase(Locale.ENGLISH), new ArrayList<>(Arrays.asList(args).subList(3, args.length)));
                case "set", "addline" ->
                        ActionHandler.tabComplete(sender, args[3].toLowerCase(Locale.ENGLISH), new ArrayList<>(Arrays.asList(args).subList(4, args.length)));
                default -> List.of();
            };
        };
    }

    //it actions displayUses [boolean]
    private void displayUses(Player p, String label, String[] args, ItemStack item) {
        TagItem tagItem = ItemTag.getTagItem(item);
        boolean value = args.length >= 3 ? Aliases.BOOLEAN.convertAlias(args[2]) : !ActionsUtility.getDisplayUses(tagItem);
        ActionsUtility.setDisplayUses(tagItem, value);
        ActionsUtility.updateUsesDisplay(item);
        if (value) {
            sendLanguageString("displayuses.feedback.set", p);
            ActionsUtility.updateUsesDisplay(item);
        } else {
            sendLanguageString("displayuses.feedback.unset", p);
            ActionsUtility.updateUsesDisplay(item);
        }
    }

    //it actions visualcooldown [boolean]
    private void visualCooldown(Player sender, String label, String[] args, ItemStack item) {
        TagItem tagItem = ItemTag.getTagItem(item);
        boolean value = args.length >= 3 ? Aliases.BOOLEAN.convertAlias(args[2]) : !ActionsUtility.getVisualCooldown(tagItem);
        ActionsUtility.setVisualCooldown(tagItem, value);
        if (value) {
            sendLanguageString("visualcooldown.feedback.set", sender);
        } else {
            sendLanguageString("visualcooldown.feedback.unset", sender);
        }
    }

    //it actions consume [boolean]
    private void consume(Player sender, String label, String[] args, ItemStack item) {
        TagItem tagItem = ItemTag.getTagItem(item);
        boolean value = args.length >= 3 ? Aliases.BOOLEAN.convertAlias(args[2]) : !ActionsUtility.getConsume(tagItem);
        ActionsUtility.setConsume(tagItem, value);
        if (value) {
            sendLanguageString("consume.feedback.set", sender);
        } else {
            sendLanguageString("consume.feedback.unset", sender);
        }
    }

    // itemtag actions setpermission <permission>
    private void permission(Player p, String label, String[] args, ItemStack item) {
        try {
            if (args.length > 3) {
                throw new IllegalArgumentException("Wrong param number");
            }
            String permission = args.length == 2 ? null : args[2].toLowerCase(Locale.ENGLISH);
            TagItem tagItem = ItemTag.getTagItem(item);
            ActionsUtility.setPermission(tagItem, permission);
            if (permission != null) {
                sendLanguageString("permission.feedback.set", p, "%permission%", permission);
            } else {
                sendLanguageString("permission.feedback.removed", p);
            }
        } catch (Exception e) {
            Util.sendComponent(p, this.craftFailFeedback(label, translateOrEmptyRaw("permission.params", p),
                    translateList("permission.description", p)));
        }
    }

    private void cooldownId(Player p, String label, String[] args, ItemStack item) {
        try {
            if (args.length > 3) {
                throw new IllegalArgumentException("Wrong param number");
            }
            String cooldownId = args.length == 2 ? null : args[2].toLowerCase(Locale.ENGLISH);
            TagItem tagItem = ItemTag.getTagItem(item);
            ActionsUtility.setCooldownId(tagItem, cooldownId);
            if (cooldownId != null) {
                sendLanguageString("cooldownid.feedback.set", p, "%id%", cooldownId);
            } else {
                sendLanguageString("cooldownid.feedback.removed", p);
            }
        } catch (Exception e) {
            Util.sendComponent(p, this.craftFailFeedback(label, translateOrEmptyRaw("cooldownid.params", p),
                    translateList("cooldownid.description", p)));
        }
    }

    //it action cooldownmsg [message  ]
    private void cooldownMsg(Player p, String label, String[] args, ItemStack item) {
        try {
            String cooldownMsg = args.length == 2 ? null : String.join(" ", Arrays.copyOfRange(args, 2, args.length));
            TagItem tagItem = ItemTag.getTagItem(item);
            ActionsUtility.setCooldownMsg(tagItem, cooldownMsg);
            if (cooldownMsg != null) {
                sendLanguageString("cooldownmsg.feedback.set", p, "%msg%", cooldownMsg);
            } else {
                sendLanguageString("cooldownmsg.feedback.removed", p);
            }
        } catch (Exception e) {
            Util.sendComponent(p, this.craftFailFeedback(label, translateOrEmptyRaw("cooldownmsg.params", p),
                    translateList("cooldownmsg.description", p)));
        }
    }

    private void cooldownMsgType(Player p, String label, String[] args, ItemStack item) {
        try {
            if (args.length != 3) {
                throw new IllegalArgumentException("Wrong param number");
            }
            String type = args[2].toLowerCase(Locale.ENGLISH);
            switch (type) {
                case "actionbar":
                case "chat":
                    break;
                default:
                    throw new IllegalArgumentException("Wrong type");
            }
            TagItem tagItem = ItemTag.getTagItem(item);
            ActionsUtility.setCooldownMsgType(tagItem, type);
            sendLanguageString("cooldownmsgtype.feedback.set", p, "%type%", type);
        } catch (Exception e) {
            Util.sendComponent(p, this.craftFailFeedback(label, translateOrEmptyRaw("cooldownmsgtype.params", p),
                    translateList("cooldownmsgtype.description", p)));
        }
    }

    private void cooldown(Player p, String label, String[] args, ItemStack item) {
        try {
            if (args.length > 3) {
                throw new IllegalArgumentException("Wrong param number");
            }
            int cooldownMs = args.length == 2 ? 0 : Integer.parseInt(args[2]);
            TagItem tagItem = ItemTag.getTagItem(item);
            ActionsUtility.setCooldownMs(tagItem, cooldownMs);
            if (cooldownMs > 0) {
                sendLanguageString("cooldown.feedback.set", p, "%cooldown_ms%",
                        String.valueOf(cooldownMs), "%cooldown_seconds%", String.valueOf(cooldownMs / 1000));
            } else {
                sendLanguageString("cooldown.feedback.removed", p);
            }
        } catch (Exception e) {
            Util.sendComponent(p, this.craftFailFeedback(label, translateOrEmptyRaw("cooldown.params", p),
                    translateList("cooldown.description", p)));
        }
    }

    private void maxUses(Player p, String label, String[] args, ItemStack item) {
        try {
            if (args.length > 3) {
                throw new IllegalArgumentException("Wrong param number");
            }
            int uses = args.length == 2 ? 1 : Integer.parseInt(args[2]);
            if (uses == 0) {
                throw new IllegalArgumentException();
            }
            TagItem tagItem = ItemTag.getTagItem(item);
            ActionsUtility.setMaxUses(tagItem, uses);
            if (ActionsUtility.getDisplayUses(tagItem)) {
                ActionsUtility.updateUsesDisplay(item);
            }
            if (uses < 0) {
                sendLanguageString("maxuses.feedback.unlimited", p);
            } else {
                sendLanguageString("maxuses.feedback.set", p, "%uses%", String.valueOf(uses));
            }
        } catch (Exception e) {
            Util.sendComponent(p, this.craftFailFeedback(label, translateOrEmptyRaw("maxuses.params", p),
                    translateList("maxuses.description", p)));
        }
    }

    private void uses(Player p, String label, String[] args, ItemStack item) {
        try {
            if (args.length > 3) {
                throw new IllegalArgumentException("Wrong param number");
            }
            int uses = args.length == 2 ? 1 : Integer.parseInt(args[2]);
            if (uses == 0) {
                throw new IllegalArgumentException();
            }
            TagItem tagItem = ItemTag.getTagItem(item);
            ActionsUtility.setUses(tagItem, uses);
            if (ActionsUtility.getDisplayUses(tagItem)) {
                ActionsUtility.updateUsesDisplay(item);
            }
            if (uses < 0) {
                sendLanguageString("uses.feedback.unlimited", p);
            } else {
                sendLanguageString("uses.feedback.set", p,
                        "%uses%", String.valueOf(uses));
            }
        } catch (Exception e) {
            Util.sendComponent(p, this.craftFailFeedback(label, translateOrEmptyRaw("uses.params", p),
                    translateList("uses.description", p)));
        }
    }

    private void invalidAction(Player p, String actionError) {
        Component message = translateOrEmpty("invalid-action.message", p, "%error%", actionError);
        Component hover = translateOrEmpty("invalid-action.hover-pre", p).append(Component.newline());
        Component color1 = translateOrEmpty("invalid-action.first_color", p);
        Component color2 = translateOrEmpty("invalid-action.second_color", p);
        boolean color = true;
        int counter = 0;
        for (String actionType : ActionHandler.getTypes()) {
            counter += actionType.length() + 1;
            hover = hover.append(color ? color1 : color2).append(Component.text(actionType));
            color = !color;
            if (counter > 30) {
                counter = 0;
                hover = hover.append(Component.newline());
            } else {
                hover = hover.append(Component.space());
            }
        }
        Util.sendComponent(p, Util.asHover(message, hover));
    }

    private void invalidActionInfo(Player p, String actionType, String actionInfo) {
        Component message = translateOrEmpty("invalid-actioninfo.message", p,
                "%error%", actionInfo, "%action%", actionType);
        Component hover = Component.join(JoinConfiguration.separator(Component.newline()),
                UtilsString.fix(ActionHandler.getAction(actionType).getInfo(), p, true));
        Util.sendComponent(p, Util.asHover(message, hover));
    }

    private void set(Player p, String label, String[] args, ItemStack item) {
        try {
            if (args.length < 4) {
                throw new IllegalArgumentException("Wrong param number");
            }
            int line = Integer.parseInt(args[2]) - 1;
            if (line < 0) {
                throw new IllegalArgumentException();
            }
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
            String action = actionType + ActionsUtility.TYPE_SEPARATOR + actionInfo;
            TagItem tagItem = ItemTag.getTagItem(item);

            if (!ActionsUtility.hasActions(tagItem)) {
                ActionsUtility.setActions(tagItem, Collections.singletonList(action));
            } else {
                List<String> list = new ArrayList<>(ActionsUtility.getActions(tagItem));
                list.set(line, action);
                ActionsUtility.setActions(tagItem, list);
            }
            sendLanguageString("set.feedback", p, "%line%",
                    String.valueOf(line + 1), "%action%", originalAction);
        } catch (Exception e) {
            Util.sendComponent(p, this.craftFailFeedback(label, translateOrEmptyRaw("set.params", p),
                    translateList("set.description", p)));
        }
    }

    private void remove(Player p, String label, String[] args, ItemStack item) {
        try {
            if (args.length != 3) {
                throw new IllegalArgumentException("Wrong param number");
            }
            int line = Integer.parseInt(args[2]) - 1;
            if (line < 0) {
                throw new IllegalArgumentException();
            }
            TagItem tagItem = ItemTag.getTagItem(item);
            String action;
            if (!ActionsUtility.hasActions(tagItem)) {
                throw new IllegalArgumentException();
            } else {
                List<String> list = new ArrayList<>(ActionsUtility.getActions(tagItem));
                action = list.remove(line);
                ActionsUtility.setActions(tagItem, list);
            }
            sendLanguageString("remove.feedback", p, "%line%",
                    String.valueOf(line + 1), "%action%", action.split(ActionsUtility.TYPE_SEPARATOR)[0]);
        } catch (Exception e) {
            Util.sendComponent(p, this.craftFailFeedback(label, translateOrEmptyRaw("remove.params", p),
                    translateList("remove.description", p)));
        }
    }

    private void add(Player p, String label, String[] args, ItemStack item) {
        try {
            if (args.length < 3) {
                throw new IllegalArgumentException("Wrong param number");
            }
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
            String action = actionType + ActionsUtility.TYPE_SEPARATOR + actionInfo;
            TagItem tagItem = ItemTag.getTagItem(item);
            if (!ActionsUtility.hasActions(tagItem)) {
                ActionsUtility.setActions(tagItem, Collections.singletonList(action));
            } else {
                List<String> list = new ArrayList<>(ActionsUtility.getActions(tagItem));
                list.add(action);
                ActionsUtility.setActions(tagItem, list);
            }
            sendFeedback(p, "add.feedback", "%action%",
                    originalAction);
        } catch (Exception e) {
            log.warn("Error in Actions", e);
            Util.sendComponent(p, this.craftFailFeedback(label, translateOrEmptyRaw("add.params", p),
                    translateList("add.description", p)));
        }
    }

    private void addLine(Player p, String label, String[] args, ItemStack item) {
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
            String action = actionType + ActionsUtility.TYPE_SEPARATOR + actionInfo;
            TagItem tagItem = ItemTag.getTagItem(item);
            if (!ActionsUtility.hasActions(tagItem)) {
                ActionsUtility.setActions(tagItem, Collections.singletonList(action));
            } else {
                List<String> list = new ArrayList<>(ActionsUtility.getActions(tagItem));
                list.add(line, action);
                ActionsUtility.setActions(tagItem, list);
            }
            sendLanguageString("addline.feedback", p, "%action%",
                    originalAction, "%line%", String.valueOf(line + 1));
        } catch (Exception e) {
            Util.sendComponent(p, this.craftFailFeedback(label, translateOrEmptyRaw("addline.params", p),
                    translateList("addline.description", p)));
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    private void event(PlayerInteractEvent event) {
        switch (event.getAction()) {
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                ItemStack item = event.getItem();
                TagItem tagItem = ItemTag.getTagItem(item);
                if (!ActionsUtility.hasActions(tagItem)) {
                    return;
                }

                String permission = ActionsUtility.getPermission(tagItem);
                if (permission != null && !event.getPlayer().hasPermission(permission)) {
                    return;
                }
                long cooldown = ActionsUtility.getCooldownMs(tagItem);
                if (cooldown > 0) {
                    String cooldownId = ActionsUtility.getCooldownId(tagItem);
                    if (ItemTag.get().getCooldownAPI().hasCooldown(event.getPlayer(), cooldownId)) {
                        String rawMsg = ActionsUtility.getCooldownMsg(tagItem);
                        if (rawMsg == null) {
                            return;
                        }
                        String type = ActionsUtility.getCooldownMsgType(tagItem);
                        Component msg = UtilsString.fix(rawMsg, event.getPlayer(), true);
                        switch (type) {
                            case "chat":
                                Util.sendComponent(event.getPlayer(), msg);
                                break;
                            case "actionbar":
                                event.getPlayer().sendActionBar(msg);
                                break;
                            default:
                                ItemTag.get().log("Invalid action cooldown message type <yellow>" + type);
                        }

                        return;
                    }
                    ItemTag.get().getCooldownAPI().setCooldown(event.getPlayer(), cooldownId, cooldown, TimeUnit.MILLISECONDS);
                    if (ActionsUtility.getVisualCooldown(tagItem)) {
                        event.getPlayer().setCooldown(item.getType(), (int) (cooldown / 50));
                    }
                }

                int uses = ActionsUtility.getUses(tagItem);
                if (uses == 0) {
                    return;
                }

                for (String action : ActionsUtility.getActions(tagItem))
                    try {
                        if (action.isEmpty()) {
                            continue;
                        }
                        ActionHandler.handleAction(event.getPlayer(), action.split(ActionsUtility.TYPE_SEPARATOR)[0],
                                action.split(ActionsUtility.TYPE_SEPARATOR)[1]);
                    } catch (Exception e) {
                        log.warn("Error in Actions", e);
                    }
                event.setCancelled(true);
                //event.setUseItemInHand(Event.Result.DENY);  TODO config
                if (uses > 0) {
                    if (uses == 1 && ActionsUtility.getConsume(tagItem)) {
                        if (event.getItem().getAmount() == 1) { //1.8 doesn't like  event.getItem().setAmount(event.getItem().getAmount() - 1); on single items
                            try {
                                if (event.getHand() == EquipmentSlot.HAND) {
                                    event.getPlayer().getInventory().setItemInMainHand(null);
                                } else {
                                    event.getPlayer().getInventory().setItemInOffHand(null);
                                }
                            } catch (Error e) {
                                event.getPlayer().getInventory().setItemInHand(null);
                            }
                        } else {
                            ItemStack clone = event.getItem().clone();
                            clone.setAmount(clone.getAmount() - 1);
                            try {
                                if (event.getHand() == EquipmentSlot.HAND) {
                                    event.getPlayer().getInventory().setItemInMainHand(clone);
                                } else {
                                    event.getPlayer().getInventory().setItemInOffHand(clone);
                                }
                            } catch (Error e) {
                                event.getPlayer().getInventory().setItemInHand(clone);
                            }
                        }
                    } else {
                        try {
                            if (event.getItem().getAmount() == 1) {
                                ActionsUtility.setUses(tagItem, uses - 1);
                                if (ActionsUtility.getDisplayUses(tagItem)) {
                                    ActionsUtility.updateUsesDisplay(item);
                                }
                            } else {
                                ItemStack clone = event.getItem();
                                clone.setAmount(clone.getAmount() - 1);
                                if (event.getHand() == EquipmentSlot.HAND) {
                                    event.getPlayer().getInventory().setItemInMainHand(clone);
                                } else {
                                    event.getPlayer().getInventory().setItemInOffHand(clone);
                                }
                                ActionsUtility.setUses(ItemTag.getTagItem(clone), uses - 1);
                                if (ActionsUtility.getDisplayUses(tagItem)) {
                                    ActionsUtility.updateUsesDisplay(clone);
                                }
                                InventoryUtils.giveAmount(event.getPlayer(), clone, 1, InventoryUtils.ExcessMode.DROP_EXCESS);
                            }

                        } catch (Throwable t) { //1.8 compability
                            if (event.getItem().getAmount() == 1) {
                                ActionsUtility.setUses(tagItem, uses - 1);
                            } else {
                                ItemStack clone = event.getItem();
                                clone.setAmount(clone.getAmount() - 1);
                                event.getPlayer().getInventory().setItemInHand(clone);
                                ActionsUtility.setUses(ItemTag.getTagItem(clone), uses - 1);
                                //has no displayuses on 1.8
                                InventoryUtils.giveAmount(event.getPlayer(), clone, 1, InventoryUtils.ExcessMode.DROP_EXCESS);
                            }
                        }
                    }
                }
            default:
        }
    }
}
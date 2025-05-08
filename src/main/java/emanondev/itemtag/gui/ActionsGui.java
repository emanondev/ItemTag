package emanondev.itemtag.gui;

import emanondev.itemedit.Util;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.gui.Gui;
import emanondev.itemedit.gui.PagedGui;
import emanondev.itemedit.utility.ItemUtils;
import emanondev.itemedit.utility.VersionUtils;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.TagItem;
import emanondev.itemtag.actions.ActionsUtility;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActionsGui implements Gui {
    private final TagItem tagItem;
    private final Inventory inventory;
    private final Player target;
    private int editorCooldown = 1;
    private int editorValue = 1;
    private String commandAlias;

    public ActionsGui(Player target, ItemStack item, String commandAlias) {
        String title = this.getLanguageMessage("gui.actions.title",
                "%player_name%", target.getName());
        this.inventory = Bukkit.createInventory(this, 2 * 9, title);
        this.target = target;
        tagItem = ItemTag.getTagItem(item);
        this.commandAlias = commandAlias;
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
                        ActionsUtility.setUses(this.tagItem, Math.max(-1, ActionsUtility.getUses(tagItem) - editorValue));
                        if (ActionsUtility.getDisplayUses(tagItem)) {
                            ActionsUtility.updateUsesDisplay(tagItem.getItem());
                        }
                        break;
                    case SHIFT_RIGHT:
                        editorValue = Math.min(1000000, editorValue * 10);
                        break;
                    case RIGHT:
                        ActionsUtility.setUses(this.tagItem, (int) Math.min(Integer.MAX_VALUE, ActionsUtility.getUses(tagItem) + (long) editorValue));
                        if (ActionsUtility.getDisplayUses(tagItem)) {
                            ActionsUtility.updateUsesDisplay(tagItem.getItem());
                        }
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
                        ActionsUtility.setMaxUses(this.tagItem, Math.max(-1, ActionsUtility.getMaxUses(tagItem) - editorValue));
                        if (ActionsUtility.getDisplayUses(this.tagItem))
                            ActionsUtility.updateUsesDisplay(tagItem.getItem());
                        break;
                    case SHIFT_RIGHT:
                        editorValue = Math.min(1000000, editorValue * 10);
                        break;
                    case RIGHT:
                        ActionsUtility.setMaxUses(this.tagItem, (int) Math.min(Integer.MAX_VALUE, ActionsUtility.getMaxUses(tagItem) + (long) editorValue));
                        if (ActionsUtility.getDisplayUses(this.tagItem))
                            ActionsUtility.updateUsesDisplay(tagItem.getItem());
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
                ActionsUtility.setConsume(tagItem, !ActionsUtility.getConsume(tagItem));
                updateInventory();
                return;
            }
            case 5: {//permission
                if (event.getClick() == ClickType.SHIFT_RIGHT) {
                    ActionsUtility.setPermission(tagItem, null);
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
                        ActionsUtility.setCooldownMs(this.tagItem,
                                Math.max(0, ActionsUtility.getCooldownMs(tagItem)
                                        - editorCooldown * 1000));
                        break;
                    case SHIFT_RIGHT:
                        editorCooldown = Math.min(1000000, editorCooldown * 10);
                        break;
                    case RIGHT:
                        ActionsUtility.setCooldownMs(this.tagItem,
                                (int) Math.min(Integer.MAX_VALUE, ActionsUtility.getCooldownMs(tagItem)
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
                    ActionsUtility.setCooldownId(tagItem, null);
                    updateInventory();
                    return;
                }
                getTargetPlayer().closeInventory();
                sendClickableText(this, "cooldownid");
                return;
            }
            case 15: {//cooldown display
                ActionsUtility.setVisualCooldown(tagItem, !ActionsUtility.getVisualCooldown(tagItem));
                updateInventory();
                return;
            }
            case 14: {//display uses
                ActionsUtility.setDisplayUses(tagItem, !ActionsUtility.getDisplayUses(tagItem));
                if (ActionsUtility.getDisplayUses(this.tagItem))
                    ActionsUtility.updateUsesDisplay(tagItem.getItem());
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
        meta = this.loadLanguageDescription(ItemUtils.getMeta(item), "gui.actions.consume",
                "%value%", Aliases.BOOLEAN.getName(ActionsUtility.getConsume(tagItem)));
        if (!ActionsUtility.getConsume(tagItem))
            meta.addEnchant(Enchantment.LURE, 1, true);
        else
            meta.removeEnchant(Enchantment.LURE);
        item.setItemMeta(meta);
        this.getInventory().setItem(7, item);

        // uses
        this.getInventory().setItem(8, this.loadLanguageDescription(this.getGuiItem("gui.actions.uses", Material.IRON_PICKAXE), "gui.actions.uses",
                "%value%", ActionsUtility.getUses(tagItem) == -1 ? "-1 (Unlimited)" : String.valueOf(ActionsUtility.getUses(tagItem)), "%editor%", String.valueOf(editorValue)
                , "%editor-prev%", String.valueOf(Math.max(1, editorValue / 10)), "%editor-next%", String.valueOf(Math.min(1000000, editorValue * 10))));

        if (VersionUtils.isVersionAfter(1, 9)) {
            //max uses
            this.getInventory().setItem(17, this.loadLanguageDescription(this.getGuiItem("gui.actions.maxuses", Material.DIAMOND_PICKAXE), "gui.actions.maxuses",
                    "%value%", ActionsUtility.getMaxUses(tagItem) == -1 ? "-1 (Unlimited)" : String.valueOf(ActionsUtility.getMaxUses(tagItem)), "%editor%", String.valueOf(editorValue)
                    , "%editor-prev%", String.valueOf(Math.max(1, editorValue / 10)), "%editor-next%", String.valueOf(Math.min(1000000, editorValue * 10))));


            item = this.getGuiItem("gui.actions.displayuses", Material.PAINTING);
            meta = this.loadLanguageDescription(ItemUtils.getMeta(item), "gui.actions.displayuses", "%value%", Aliases.BOOLEAN.getName(ActionsUtility.getDisplayUses(tagItem)));
            if (ActionsUtility.getDisplayUses(tagItem))
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
                    "%value%", ActionsUtility.getPermission(tagItem) == null ? "<none>" : ActionsUtility.getPermission(tagItem)));
        } catch (Error e) {
            this.getInventory().setItem(5, this.loadLanguageDescription(this.getGuiItem("gui.actions.permission", Material.valueOf("IRON_FENCE")), "gui.actions.permission",
                    "%value%", ActionsUtility.getPermission(tagItem) == null ? "<none>" : ActionsUtility.getPermission(tagItem)));
        }

        // cooldown
        this.getInventory().setItem(6, this.loadLanguageDescription(this.getGuiItem("gui.actions.cooldown", Material.COMPASS), "gui.actions.cooldown",
                "%value_s%", String.valueOf(ActionsUtility.getCooldownMs(tagItem) / 1000), "%editor%", String.valueOf(editorCooldown)
                , "%editor-prev%", String.valueOf(Math.max(1, editorCooldown / 10)), "%editor-next%", String.valueOf(Math.min(1000000, editorCooldown * 10))));

        //cooldownid
        this.getInventory().setItem(16, this.loadLanguageDescription(this.getGuiItem("gui.actions.cooldownid", Material.NAME_TAG), "gui.actions.cooldownid",
                "%value%", ActionsUtility.getCooldownId(tagItem)));

        //visualcooldown
        item = this.getGuiItem("gui.actions.visualcooldown", Material.ENDER_PEARL);
        meta = this.loadLanguageDescription(ItemUtils.getMeta(item), "gui.actions.visualcooldown", "%value%", Aliases.BOOLEAN.getName(ActionsUtility.getVisualCooldown(tagItem)));
        if (ActionsUtility.getVisualCooldown(tagItem)) {
            meta.addEnchant(Enchantment.LURE, 1, true);
        } else {
            meta.removeEnchant(Enchantment.LURE);
        }
        item.setItemMeta(meta);
        this.getInventory().setItem(15, item);

        //info
        item = this.getGuiItem("gui.actions.info", Material.PAPER);
        meta = this.loadLanguageDescription(ItemUtils.getMeta(item), "gui.actions.info");
        List<String> lore = new ArrayList<>(meta.hasLore() ? meta.getLore() : Collections.emptyList());
        List<String> list = ActionsUtility.getActions(tagItem);
        if (list != null) {
            for (String action : list) {
                if (action != null && !action.isEmpty()) { //this line is for compatibility
                    int index = action.indexOf(ActionsUtility.TYPE_SEPARATOR);
                    String actionPre = action.substring(0, index);
                    String actionPost = action.substring(index + ActionsUtility.TYPE_SEPARATOR.length());
                    if (actionPost.startsWith("-pin")) {
                        try {
                            actionPost = actionPost.substring(69);
                        } catch (Exception ignored) {
                        }
                    }
                    lore.add(ChatColor.YELLOW + actionPre + " " + ChatColor.WHITE + actionPost);
                }
            }
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        this.getInventory().setItem(2, item);
    }

    private void sendClickableText(Gui gui, String postClickable) {
        Util.sendMessage(gui.getTargetPlayer(), new ComponentBuilder(gui.getLanguageMessage("gui.actions.click-interact")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(gui.getLanguageMessage("gui.actions.click-hover")).create()))
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/it " + commandAlias + " " + postClickable + " ")).create());
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
            List<String> actionsList = ActionsUtility.getActions(tagItem);
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
            List<String> actions = ActionsUtility.getActions(tagItem);
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
                        String action = actions.get(event.getSlot());
                        int index = action.indexOf(ActionsUtility.TYPE_SEPARATOR);
                        String actionPre = action.substring(0, index);
                        String actionPost = action.substring(index + ActionsUtility.TYPE_SEPARATOR.length());
                        if (actionPost.startsWith("-pin")) {
                            try {
                                actionPost = actionPost.substring(69);
                            } catch (Exception ignored) {
                            }
                        }
                        getTargetPlayer().closeInventory();
                        sendClickableText(this, "set "
                                + (event.getSlot() + 1) + " " + actionPre + " " + actionPost);
                        return;
                    case LEFT:
                        getTargetPlayer().closeInventory();
                        sendClickableText(this, "addline " + (event.getSlot() + 1));
                        return;
                    case SHIFT_RIGHT:
                        actions = new ArrayList<>(actions);
                        actions.remove(event.getSlot());
                        ActionsUtility.setActions(tagItem, actions);
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
            final List<String> actions = ActionsUtility.getActions(tagItem);
            //add actions
            if (actions != null) {
                for (int i = 0; i < 45; i++) {
                    int elIndex = (page - 1) * ROWS * 9 + i;
                    if (elIndex >= actions.size()) {
                        this.inventory.setItem(i, null);
                        continue;
                    }
                    int index = actions.get(elIndex).indexOf(ActionsUtility.TYPE_SEPARATOR);
                    String actionPre = actions.get(elIndex).substring(0, index);
                    String actionPost = actions.get(elIndex).substring(index + ActionsUtility.TYPE_SEPARATOR.length());
                    if (actionPost.startsWith("-pin")) {
                        try {
                            actionPost = actionPost.substring(69);
                        } catch (Exception ignored) {
                        }
                    }
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
                    item.setAmount(Math.max(125, elIndex + 1));
                    item.setItemMeta(meta);
                    this.inventory.setItem(i, item);
                }
            }
            //add action button
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
                //this.inventory.setItem(actions == null ? 1 : actions.size() + 1, null);
            }
            this.inventory.setItem(49, this.getBackItem());
            //here arrows
            if (page > 1) {
                this.inventory.setItem(ROWS * 9 + 1, getPreviousPageItem());
            } else {
                this.inventory.setItem(ROWS * 9 + 1, null);
            }
            if (page < maxPages) {
                this.inventory.setItem(ROWS * 9 + 7, getNextPageItem());
            } else {
                this.inventory.setItem(ROWS * 9 + 1, null);
            }
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
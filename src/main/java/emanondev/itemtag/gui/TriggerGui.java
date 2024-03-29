package emanondev.itemtag.gui;

import emanondev.itemedit.gui.PagedGui;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.TagItem;
import emanondev.itemtag.activity.Activity;
import emanondev.itemtag.activity.TriggerHandler;
import emanondev.itemtag.activity.TriggerType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TriggerGui implements PagedGui {


    private static final int TRIGGER_SLOTS = 5 * 9;
    private final Inventory inventory;
    private final Player target;
    private final ItemStack item;
    private final List<TriggerType> triggers = new ArrayList<>();
    private int page = 1;


    public TriggerGui(Player target, ItemStack item) {
        String title = this.getLanguageMessage("gui.triggers.title_main",
                "%player_name%", target.getName());
        this.item = item;
        this.inventory = Bukkit.createInventory(this, TRIGGER_SLOTS + 9, title);
        this.target = target;
        this.triggers.addAll(TriggerHandler.getTriggers());
        this.triggers.sort(Comparator.comparing(TriggerType::getId));
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

    @Override
    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        page = Math.max(1, Math.min(page, getMaxPage()));
        if (page != this.page) {
            this.inventory.clear();
            this.page = page;
            updateInventory();
        }
    }

    public int getMaxPage() {
        return triggers.size() / TRIGGER_SLOTS +
                (triggers.size() % TRIGGER_SLOTS == 0 ? 0 : 1);
    }

    @Override
    public void onClose(InventoryCloseEvent inventoryCloseEvent) {

    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != this.inventory)
            return;
        if (triggers.size() <= TRIGGER_SLOTS * (page - 1) + event.getSlot())
            return;
        TriggerType type = triggers.get(TRIGGER_SLOTS * (page - 1) + event.getSlot());
        TagItem tagItem = ItemTag.getTagItem(getTargetPlayer().getItemInHand());//TODO
        //if (TriggerHandler.hasTrigger(type,tagItem))
        Activity activity = TriggerHandler.getTriggerActivity(type, tagItem);
        if (activity == null) {
            //TODO select activity
        } else {
            getTargetPlayer().openInventory(new ActivityGui(activity, getTargetPlayer(), this).getInventory());
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
        for (int i = 0; TRIGGER_SLOTS * (page - 1) + i < triggers.size(); i++) {
            inventory.setItem(i, triggers.get(TRIGGER_SLOTS * (page - 1) + i).getGuiItem(getTargetPlayer()));
        }
    }
}

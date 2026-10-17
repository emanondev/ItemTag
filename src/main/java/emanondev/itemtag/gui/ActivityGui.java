package emanondev.itemtag.gui;

import emanondev.itemedit.gui.Gui;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.activity.Activity;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ActivityGui implements Gui {
    private final Inventory inventory;
    private final Player target;
    private final Activity activity;
    private final TriggerGui parent;

    public ActivityGui(@NotNull Activity activity, @NotNull Player target, @Nullable TriggerGui parent) {
        this.target = target;
        this.activity = activity;
        this.parent = parent;
        Component title = this.getLanguageComponent("gui.activity.title_main",
                "%player_name%", target.getName(), "%activity_id%", activity.getId());
        this.inventory = Bukkit.createInventory(this, 6 * 9, title);
    }

    @Override
    public void onClose(InventoryCloseEvent inventoryCloseEvent) {

    }

    @Override
    public void onClick(InventoryClickEvent inventoryClickEvent) {

    }

    @Override
    public void onDrag(InventoryDragEvent inventoryDragEvent) {

    }

    @Override
    public void onOpen(InventoryOpenEvent inventoryOpenEvent) {

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

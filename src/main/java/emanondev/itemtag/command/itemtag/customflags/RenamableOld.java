package emanondev.itemtag.command.itemtag.customflags;

import emanondev.itemtag.ItemTag;
import emanondev.itemtag.command.itemtag.Flag;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class RenamableOld extends CustomFlag {

    private final static String RENAMABLE_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":renamable";

    public RenamableOld(Flag cmd) {
        super("renamable", RENAMABLE_KEY, cmd);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void event(InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.ANVIL) {
            return;
        }
        if (ItemTag.getTagItem(event.getInventory().getItem(0)).hasBooleanTag(RENAMABLE_KEY) ||
                ItemTag.getTagItem(event.getInventory().getItem(1)).hasBooleanTag(RENAMABLE_KEY)) {
            Bukkit.getScheduler().runTaskLater(ItemTag.get(), () -> {
                if (ItemTag.getTagItem(event.getInventory().getItem(0)).hasBooleanTag(RENAMABLE_KEY) ||
                        ItemTag.getTagItem(event.getInventory().getItem(1)).hasBooleanTag(RENAMABLE_KEY)) {
                    event.getInventory().setItem(2, null);
                }
            }, 1L);
            if (event.getSlot() == 2) {
                event.setCancelled(true);
            }
        }

    }

    @Override
    public ItemStack getGuiItem() {
        return new ItemStack(Material.NAME_TAG);
    }
}

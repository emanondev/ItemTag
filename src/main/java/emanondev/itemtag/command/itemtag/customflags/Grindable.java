package emanondev.itemtag.command.itemtag.customflags;

import emanondev.itemedit.UtilLegacy;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.command.itemtag.Flag;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class Grindable extends CustomFlag {

    private final static String GRINDABLE_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":grindable";

    public Grindable(Flag cmd) {
        super("grindable", GRINDABLE_KEY, cmd);
    }

    @EventHandler
    public void event(InventoryClickEvent event) {
        Inventory inv = UtilLegacy.getTopInventory(event);
        if (!(inv instanceof GrindstoneInventory))
            return;
        boolean found = ItemTag.getTagItem(inv.getItem(0)).hasBooleanTag(GRINDABLE_KEY)
                || ItemTag.getTagItem(inv.getItem(1)).hasBooleanTag(GRINDABLE_KEY);
        if (!found)
            return;
        if (event.getSlotType() == SlotType.RESULT) {
            event.setCancelled(true);
            //TODO lascia glich visivo quando usi F
        }
    }

    @Override
    public ItemStack getGuiItem() {
        return new ItemStack(Material.GRINDSTONE);
    }
}

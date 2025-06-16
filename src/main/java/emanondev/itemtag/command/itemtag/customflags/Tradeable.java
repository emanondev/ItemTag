package emanondev.itemtag.command.itemtag.customflags;

import emanondev.itemtag.ItemTag;
import emanondev.itemtag.command.itemtag.Flag;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;

import java.util.Locale;

public class Tradeable extends CustomFlag {

    private final static String TRADEABLE_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":tradeable";

    public Tradeable(Flag cmd) {
        super("tradeable", TRADEABLE_KEY, cmd);
    }

    @EventHandler
    public void event(InventoryClickEvent event) {
        if (!(event.getClickedInventory() instanceof MerchantInventory)) {
            return;
        }
        if (event.getSlotType() != InventoryType.SlotType.RESULT) {
            return;
        }
        MerchantInventory inv = (MerchantInventory) event.getClickedInventory();
        if (ItemTag.getTagItem(inv.getItem(0)).hasBooleanTag(TRADEABLE_KEY) ||
                ItemTag.getTagItem(inv.getItem(1)).hasBooleanTag(TRADEABLE_KEY)) {
            event.setCancelled(true);
        }
    }

    @Override
    public ItemStack getGuiItem() {
        return new ItemStack(Material.EMERALD);
    }
}

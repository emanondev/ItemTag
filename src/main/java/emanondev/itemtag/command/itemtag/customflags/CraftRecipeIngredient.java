package emanondev.itemtag.command.itemtag.customflags;

import emanondev.itemedit.utility.InventoryUtils;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.command.itemtag.Flag;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class CraftRecipeIngredient extends CustomFlag {

    private final static String CRAFTING_INGREDIENT_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":craft_ingredient";

    public CraftRecipeIngredient(Flag cmd) {
        super("recipeingredient", CRAFTING_INGREDIENT_KEY, cmd);
    }

    @EventHandler(ignoreCancelled = true)
    public void event(CraftItemEvent event) {
        for (ItemStack item : event.getInventory().getMatrix()) {
            if (ItemTag.getTagItem(item).hasBooleanTag(CRAFTING_INGREDIENT_KEY)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void event(InventoryClickEvent event) {
        Inventory inv = InventoryUtils.getTopInventory(event);
        if (inv == null || !inv.getType().name().equals("CARTOGRAPHY")) {
            return;
        }

        if (event.getSlotType() != InventoryType.SlotType.RESULT) {
            return;
        }

        if (ItemTag.getTagItem(inv.getItem(0)).hasBooleanTag(CRAFTING_INGREDIENT_KEY)) {
            event.setCancelled(true);
            return;
        }
        if (ItemTag.getTagItem(inv.getItem(1)).hasBooleanTag(CRAFTING_INGREDIENT_KEY)) {
            event.setCancelled(true);
        }
    }

    @Override
    public ItemStack getGuiItem() {
        return new ItemStack(Material.CRAFTING_TABLE);
    }
}

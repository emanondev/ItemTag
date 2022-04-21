package emanondev.itemtag.command.itemtag.customflags;

import emanondev.itemtag.ItemTag;
import emanondev.itemtag.command.itemtag.Flag;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class CraftRecipeIngredient extends CustomFlag {

    private final static String CRAFTING_INGREDIENT_KEY = ItemTag.get().getName().toLowerCase() + ":craft_ingredient";

    public CraftRecipeIngredient(Flag cmd) {
        super("recipeingredient", CRAFTING_INGREDIENT_KEY, cmd);
    }

    @EventHandler
    public void event(CraftItemEvent event) {
        for (ItemStack item : event.getInventory().getMatrix())
            if (ItemTag.getTagItem(item).hasBooleanTag(CRAFTING_INGREDIENT_KEY)) {
                event.setCancelled(true);
                return;
            }
    }

    @Override
    public ItemStack getGuiItem() {
        return new ItemStack(Material.CRAFTING_TABLE);
    }
}

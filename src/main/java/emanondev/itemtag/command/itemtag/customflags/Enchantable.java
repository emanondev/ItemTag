package emanondev.itemtag.command.itemtag.customflags;

import emanondev.itemtag.ItemTag;
import emanondev.itemtag.command.itemtag.Flag;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;

public class Enchantable extends CustomFlag {

    private final static String ENCHANTABLE_KEY = ItemTag.get().getName().toLowerCase() + ":enchantable";

    public Enchantable(Flag cmd) {
        super("enchantable", ENCHANTABLE_KEY, cmd);
    }

    @EventHandler
    public void event(EnchantItemEvent event) {
        if (ItemTag.getTagItem(event.getItem()).hasBooleanTag(ENCHANTABLE_KEY))
            event.setCancelled(true);
    }

    @Override
    public ItemStack getGuiItem() {
        return new ItemStack(Material.ENCHANTING_TABLE);
    }
}

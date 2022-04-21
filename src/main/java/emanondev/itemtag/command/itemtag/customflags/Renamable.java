package emanondev.itemtag.command.itemtag.customflags;

import emanondev.itemtag.ItemTag;
import emanondev.itemtag.command.itemtag.Flag;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;

public class Renamable extends CustomFlag {

    private final static String RENAMABLE_KEY = ItemTag.get().getName().toLowerCase() + ":renamable";

    public Renamable(Flag cmd) {
        super("renamable", RENAMABLE_KEY, cmd);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void event(PrepareAnvilEvent event) {
        if (ItemTag.getTagItem(event.getInventory().getItem(0)).hasBooleanTag(RENAMABLE_KEY) ||
                ItemTag.getTagItem(event.getInventory().getItem(1)).hasBooleanTag(RENAMABLE_KEY))
            event.setResult(null);
    }

    @Override
    public ItemStack getGuiItem() {
        return new ItemStack(Material.NAME_TAG);
    }
}

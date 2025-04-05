package emanondev.itemtag.command.itemtag.customflags;

import emanondev.itemtag.ItemTag;
import emanondev.itemtag.command.itemtag.Flag;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class SmithingTable extends CustomFlag {
    private final static String SMITHING_TABLE = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":smithing_table";

    public SmithingTable(Flag cmd) {
        super("smithing_table", SMITHING_TABLE, cmd);
    }

    @EventHandler(ignoreCancelled = true)
    public void event(SmithItemEvent event) {
        for (ItemStack item : event.getInventory().getContents()) {
            if (ItemTag.getTagItem(item).hasBooleanTag(SMITHING_TABLE)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void event(PrepareSmithingEvent event) {
        for (ItemStack item : event.getInventory().getContents()) {
            if (ItemTag.getTagItem(item).hasBooleanTag(SMITHING_TABLE)) {
                event.setResult(null);
                return;
            }
        }
    }

    @Override
    public ItemStack getGuiItem() {
        return new ItemStack(Material.SMITHING_TABLE);
    }

}
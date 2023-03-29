package emanondev.itemtag.command.itemtag.customflags;

import emanondev.itemtag.ItemTag;
import emanondev.itemtag.command.itemtag.Flag;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class VanishCurse extends CustomFlag {

    private final static String VANISHCURSE_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":vanishcurse";

    public VanishCurse(Flag cmd) {
        super("vanishcurse", VANISHCURSE_KEY, cmd);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void event(PlayerDeathEvent event) {
        event.getDrops().removeIf((item) -> ItemTag.getTagItem(item).hasBooleanTag(VANISHCURSE_KEY));
    }

    @Override
    public boolean defaultValue() {
        return false;
    }

    @Override
    public ItemStack getGuiItem() {
        return new ItemStack(Material.BOOK);
    }
}

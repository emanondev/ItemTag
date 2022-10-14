package emanondev.itemtag.command.itemtag.customflags;

import emanondev.itemtag.ItemTag;
import emanondev.itemtag.command.itemtag.Flag;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class Placeable extends CustomFlag {

    private final static String PLACEABLE_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":placeable";

    public Placeable(Flag subCmd) {
        super("placeable", PLACEABLE_KEY, subCmd);
        reload();
    }

    @EventHandler(ignoreCancelled = true)
    private void event(BlockPlaceEvent event) {
        if (ItemTag.getTagItem(event.getItemInHand()).hasBooleanTag(PLACEABLE_KEY)) {
            event.setCancelled(true);
            if (cooldownSeconds == 0 || !getPlugin().getCooldownAPI().hasCooldown(event.getPlayer(), PLACEABLE_KEY)) {
                sendLanguageString("feedback.failed-place-attempt", null, event.getPlayer());
                if (cooldownSeconds != 0)
                    getPlugin().getCooldownAPI().setCooldownSeconds(event.getPlayer(), PLACEABLE_KEY, cooldownSeconds);
            }
        }
    }

    private long cooldownSeconds;

    public void reload() {
        cooldownSeconds = Math.max(0, this.getConfigLong(
                "failed-place-attempt-message-cooldown-seconds"));
    }

    @Override
    public ItemStack getGuiItem() {
        return new ItemStack(Material.BRICKS);
    }
}
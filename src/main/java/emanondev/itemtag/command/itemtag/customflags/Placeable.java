package emanondev.itemtag.command.itemtag.customflags;

import emanondev.itemtag.ItemTag;
import emanondev.itemtag.command.itemtag.Flag;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Placeable extends CustomFlag {

    private final static String PLACEABLE_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":placeable";
    private long cooldownSeconds;

    public Placeable(Flag cmd) {
        super("placeable", PLACEABLE_KEY, cmd);
        reload();
    }

    @EventHandler(ignoreCancelled = true)
    private void event(BlockPlaceEvent event) {
        if (ItemTag.getTagItem(event.getItemInHand()).hasBooleanTag(PLACEABLE_KEY)) {
            event.setCancelled(true);
            if (cooldownSeconds == 0 || !getPlugin().getCooldownAPI().hasCooldown(event.getPlayer(), PLACEABLE_KEY)) {
                sendLanguageString("feedback.failed-place-attempt", null, event.getPlayer());
                if (cooldownSeconds != 0) {
                    getPlugin().getCooldownAPI().setCooldown(event.getPlayer(), PLACEABLE_KEY,
                            cooldownSeconds, TimeUnit.SECONDS);
                }
            }
        }
    }

    public void reload() {
        cooldownSeconds = Math.max(0, this.getConfigLong(
                "failed-place-attempt-message-cooldown-seconds"));
    }

    @Override
    public ItemStack getGuiItem() {
        return new ItemStack(Material.BRICKS);
    }
}
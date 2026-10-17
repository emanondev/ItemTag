package emanondev.itemtag.command.itemtag.customflags;

import emanondev.itemedit.utility.InventoryUtils;
import emanondev.itemedit.utility.VersionUtils;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.command.itemtag.Flag;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class Usable extends CustomFlag {
    private final static String USABLE_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":usable";

    public Usable(Flag cmd) {
        super("usable", USABLE_KEY, cmd);
    }

    @EventHandler
    private void event(PlayerInteractEvent event) {
        switch (event.getAction()) {
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                if (ItemTag.getTagItem(event.getItem()).hasBooleanTag(USABLE_KEY)) {
                    event.setUseItemInHand(Result.DENY);
                    Block b;
                    if (event.getItem().getType() == Material.BUCKET) {
                        b = event.getPlayer().getTargetBlockExact(7, FluidCollisionMode.SOURCE_ONLY);
                    } else
                        b = null;
                    Bukkit.getScheduler().runTaskLater(ItemTag.get(), () -> { //TODO folia fix
                        if (!VersionUtils.isAfter(1, 20)) {
                            InventoryUtils.updateView(event.getPlayer());
                        }
                        if (b != null) {//reduce clientside visual glich on liquids only for 1.14+
                            event.getPlayer().sendBlockChange(b.getLocation(), b.getBlockData());
                        }
                    }, 1L);
                }
            default:
        }
    }


    @Override
    public ItemStack getGuiItem() {
        return new ItemStack(Material.STONE_BUTTON);
    }
}

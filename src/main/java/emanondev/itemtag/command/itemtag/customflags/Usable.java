package emanondev.itemtag.command.itemtag.customflags;

import emanondev.itemedit.ItemEdit;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.command.itemtag.Flag;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Usable extends CustomFlag {
    private final static String USABLE_KEY = ItemTag.get().getName().toLowerCase() + ":usable";

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
                    if (event.getItem().getType() == Material.BUCKET &&
                            ItemEdit.GAME_VERSION > 14) {
                        b = event.getPlayer().getTargetBlockExact(7, FluidCollisionMode.SOURCE_ONLY);
                    } else b = null;
                    Bukkit.getScheduler().runTaskLater(ItemTag.get(), () -> {
                        event.getPlayer().updateInventory();
                        if (b != null) //reduce clientside visual glich on liquids only for 1.14+
                            event.getPlayer().sendBlockChange(b.getLocation(), b.getBlockData());
                    }, 1L);
                }
            default:
        }
    }

    @EventHandler
    private void event(PlayerBucketFillEvent event) { //obsolete and unrequired on 1.18
        if (ItemEdit.GAME_VERSION >= 18)
            return;
        ItemStack item = this.getItemInHand(event.getPlayer());
        if (ItemEdit.GAME_VERSION == 8 && (item == null || item.getType() != Material.BUCKET))
            item = event.getPlayer().getInventory().getItemInOffHand();

        if (ItemTag.getTagItem(item).hasBooleanTag(USABLE_KEY))
            event.setCancelled(true);
    }


    @EventHandler
    private void event(PlayerBucketEmptyEvent event) { //obsolete and unrequired on 1.18
        if (ItemEdit.GAME_VERSION >= 18)
            return;
        ItemStack item = this.getItemInHand(event.getPlayer());
        if (ItemEdit.GAME_VERSION != 8 && (item == null ||
                (item.getType() != Material.LAVA_BUCKET && item.getType() != Material.WATER_BUCKET)))
            item = event.getPlayer().getInventory().getItemInOffHand();
        if (ItemTag.getTagItem(item).hasBooleanTag(USABLE_KEY))
            event.setCancelled(true);
    }

    @Override
    public ItemStack getGuiItem() {
        return new ItemStack(Material.STONE_BUTTON);
    }
}

package emanondev.itemtag.command.itemtag.customflags;

import emanondev.itemedit.utility.SchedulerUtils;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.command.itemtag.Flag;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
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

    @Override
    public ItemStack getGuiItem() {
        return new ItemStack(Material.STONE_BUTTON);
    }

    @EventHandler
    private void event(PlayerInteractEvent event) {
        switch (event.getAction()) {
            case RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK -> {
                if (ItemTag.getTagItem(event.getItem()).hasBooleanTag(USABLE_KEY)) {
                    event.setUseItemInHand(Result.DENY);
                    Player player = event.getPlayer();
                    Block b;
                    if (event.getItem().getType() == Material.BUCKET) {
                        b = player.getTargetBlockExact(7, FluidCollisionMode.SOURCE_ONLY);
                    } else
                        b = null;
                    if (b != null) {//reduce clientside visual glich on liquids only for 1.14+
                        Location blockLocation = b.getLocation();
                        BlockData blockData = b.getBlockData();
                        SchedulerUtils.runLater(ItemTag.get(), player, 1L,
                                () -> player.sendBlockChange(blockLocation, blockData));
                    }
                }
            }
        }
    }
}

package emanondev.itemtag.command.itemtag.customflags;

import emanondev.itemedit.ItemEdit;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.command.itemtag.Flag;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Breedable;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class EntityFood extends CustomFlag {
    private final static String ENTITYFOOD_KEY = ItemTag.get().getName().toLowerCase() + ":entity_food";

    public EntityFood(Flag cmd) {
        super("entityfood", ENTITYFOOD_KEY, cmd);
    }

    @EventHandler
    private void event(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Animals))
            return;
        ItemStack item = ItemEdit.GAME_VERSION==8?this.getItemInHand(event.getPlayer()):event.getPlayer().getInventory().getItem(event.getHand());
        if (!ItemTag.getTagItem(item).hasBooleanTag(ENTITYFOOD_KEY))
            return;
        if (((Animals) event.getRightClicked()).isBreedItem(item))
            event.setCancelled(true);
    }

    @Override
    public ItemStack getGuiItem() {
        return new ItemStack(Material.CARROT);
    }
}

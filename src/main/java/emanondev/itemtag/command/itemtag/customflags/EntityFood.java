package emanondev.itemtag.command.itemtag.customflags;

import emanondev.itemedit.utility.ItemUtils;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.command.itemtag.Flag;
import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class EntityFood extends CustomFlag {
    private final static String ENTITYFOOD_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":entity_food";

    public EntityFood(Flag cmd) {
        super("entityfood", ENTITYFOOD_KEY, cmd);
    }

    @EventHandler
    private void event(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Animals))
            return;
        ItemStack item = ItemUtils.getHandItem(event.getPlayer());
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

package emanondev.itemtag.command.itemtag.customflags;

import emanondev.itemedit.UtilsInventory;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.command.itemtag.Flag;
import emanondev.itemtag.equipmentchange.EquipmentChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EquipmentFlag extends CustomFlag {
    private final static String EQUIPMENT = ItemTag.get().getName().toLowerCase() + ":equipment";


    public EquipmentFlag(@NotNull Flag subCommand) {
        super("equipment", EQUIPMENT, subCommand);
    }

    @Override
    public ItemStack getGuiItem() {
        return new ItemStack(Material.IRON_CHESTPLATE);
    }

    @EventHandler
    public void event(EquipmentChangeEvent event) {
        switch (event.getSlotType()) {
            case FEET:
            case LEGS:
            case CHEST:
            case HEAD:
                break;
            default:
                return;
        }
        if (this.getValue(ItemTag.getTagItem(event.getTo())))
            Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                ItemStack item = event.getPlayer().getInventory().getItem(event.getSlotType());
                if (!getValue(ItemTag.getTagItem(event.getTo())))
                    return;
                event.getPlayer().getInventory().setItem(event.getSlotType(), null);
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_ITEM_BREAK, 1F, 1F);
                UtilsInventory.giveAmount(event.getPlayer(), item, item.getAmount(), UtilsInventory.ExcessManage.DROP_EXCESS);
            }, 1L);
    }
}

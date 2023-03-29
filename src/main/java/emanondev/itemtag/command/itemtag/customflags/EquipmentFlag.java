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

import java.util.Locale;

public class EquipmentFlag extends CustomFlag {
    private final static String EQUIPMENT = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":equipment";


    public EquipmentFlag(@NotNull Flag cmd) {
        super("equipment", EQUIPMENT, cmd);
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

        if (!this.getValue(ItemTag.getTagItem(event.getTo())))
            Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                if (!event.getPlayer().isOnline())
                    return;
                ItemStack item = event.getPlayer().getInventory().getItem(event.getSlotType()).clone();
                if (getValue(ItemTag.getTagItem(item)))
                    return;
                event.getPlayer().getInventory().setItem(event.getSlotType(), null);
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_ITEM_BREAK, 1F, 1F);
                UtilsInventory.giveAmount(event.getPlayer(), item, item.getAmount(), UtilsInventory.ExcessManage.DROP_EXCESS);
                ItemTag.get().getEquipChangeListener().onEquipChange(event.getPlayer(), EquipmentChangeEvent.EquipMethod.UNKNOWN
                        , event.getSlotType(), item, null);
            }, 1L);
    }
}

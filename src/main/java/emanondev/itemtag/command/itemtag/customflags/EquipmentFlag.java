package emanondev.itemtag.command.itemtag.customflags;

import emanondev.itemedit.utility.InventoryUtils;
import emanondev.itemedit.utility.ItemUtils;
import emanondev.itemedit.utility.SchedulerUtils;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.command.itemtag.Flag;
import emanondev.itemtag.equipmentchange.EquipmentChangeEvent;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.EquipmentSlot;
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
        if (!InventoryUtils.getPlayerEquipmentSlots().contains(event.getSlotType())) {
            return;
        }
        switch (event.getSlotType()) {
            case FEET:
            case LEGS:
            case CHEST:
            case HEAD:
                break;
            default:
                return;
        }
        if (ItemUtils.isAirOrNull(event.getTo())) {
            return;
        }
        if (this.getValue(ItemTag.getTagItem(event.getTo()))) {
            return;
        }
        Player player = event.getPlayer();
        EquipmentSlot slot = event.getSlotType();
        SchedulerUtils.runLater(getPlugin(), player, 1L, () -> {
            if (!player.isOnline()) {
                return;
            }
            ItemStack originalItem = player.getInventory().getItem(slot);

            if (ItemUtils.isAirOrNull(originalItem)) {
                return;
            }
            ItemStack item = originalItem.clone();
            if (getValue(ItemTag.getTagItem(item))) {
                return;
            }
            player.getInventory().setItem(slot, null);
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1F, 1F);
            InventoryUtils.giveAmount(player, item, item.getAmount(), InventoryUtils.ExcessMode.DROP_EXCESS);
            ItemTag.get().getEquipChangeListener().onEquipChange(
                    player, EquipmentChangeEvent.EquipMethod.UNKNOWN, slot, item, null);
        });
    }
}

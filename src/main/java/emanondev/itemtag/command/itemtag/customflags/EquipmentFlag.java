package emanondev.itemtag.command.itemtag.customflags;

import emanondev.itemedit.utility.InventoryUtils;
import emanondev.itemedit.utility.ItemUtils;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.command.itemtag.Flag;
import emanondev.itemtag.compability.SchedulerUtils;
import emanondev.itemtag.equipmentchange.EquipmentChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.EntityEquipment;
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
        SchedulerUtils.runTaskLater(event.getPlayer().getLocation(), () -> {
            if (!event.getPlayer().isOnline()) {
                return;
            }
            ItemStack originalItem = null;
            try {
                originalItem = event.getPlayer().getInventory().getItem(event.getSlotType());
            } catch (Throwable e) {
                EntityEquipment equip = event.getPlayer().getEquipment();
                if (equip != null) {
                    switch (event.getSlotType().name()) {
                        case "HAND":
                            originalItem = equip.getItemInHand();
                            break;
                        case "LEGS":
                            originalItem = equip.getLeggings();
                            break;
                        case "CHEST":
                            originalItem = equip.getChestplate();
                            break;
                        case "HEAD":
                            originalItem = equip.getHelmet();
                            break;
                        case "FEET":
                            originalItem = equip.getBoots();
                            break;
                        case "OFF_HAND":
                            originalItem = equip.getItemInOffHand();
                            break;
                        default:
                            throw new IllegalArgumentException();
                    }
                }
            }
            if (ItemUtils.isAirOrNull(originalItem)) {
                return;
            }
            ItemStack item = originalItem.clone();
            if (getValue(ItemTag.getTagItem(item))) {
                return;
            }
            event.getPlayer().getInventory().setItem(event.getSlotType(), null);
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_ITEM_BREAK, 1F, 1F);
            InventoryUtils.giveAmount(event.getPlayer(), item, item.getAmount(), InventoryUtils.ExcessMode.DROP_EXCESS);
            ItemTag.get().getEquipChangeListener().onEquipChange(event.getPlayer(), EquipmentChangeEvent.EquipMethod.UNKNOWN
                    , event.getSlotType(), item, null);
        }, 1L);
    }
}

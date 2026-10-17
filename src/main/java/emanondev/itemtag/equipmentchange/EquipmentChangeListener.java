package emanondev.itemtag.equipmentchange;

import emanondev.itemedit.utility.InventoryUtils;
import emanondev.itemedit.utility.ItemUtils;
import emanondev.itemtag.ItemTag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class EquipmentChangeListener extends EquipmentChangeListenerBase {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void event(InventoryClickEvent event) {
        ItemTag.get().log(event.getAction().name());
        if (!(event.getWhoClicked() instanceof Player p))
            return;
        if (event.getWhoClicked().hasMetadata("NPC"))
            return;
        EquipmentSlot clickedSlot = getEquipmentSlotAtPosition(event.getRawSlot(), p, event.getView());
        switch (event.getAction()) {
            case CLONE_STACK, NOTHING -> {
                return;
            }
            case DROP_ONE_CURSOR, DROP_ALL_CURSOR -> clickDrop.add(p);
            case DROP_ONE_SLOT -> {
                if (!ItemUtils.isAirOrNull(event.getCursor()))
                    return;
                if (clickedSlot != null && event.getCurrentItem().getAmount() == 1)
                    onEquipChange(p, EquipmentChangeEvent.EquipMethod.INVENTORY_DROP, clickedSlot, event.getCurrentItem(), null);
                clickDrop.add(p);
            }
            case DROP_ALL_SLOT -> {
                if (!ItemUtils.isAirOrNull(event.getCursor()))
                    return;
                if (clickedSlot != null)
                    onEquipChange(p, EquipmentChangeEvent.EquipMethod.INVENTORY_DROP, clickedSlot, event.getCurrentItem(), null);
                clickDrop.add(p);
            }
            case PICKUP_ALL -> {
                if (clickedSlot == null)
                    return;
                onEquipChange(p, EquipmentChangeEvent.EquipMethod.INVENTORY_PICKUP, clickedSlot, event.getCurrentItem(), null);
            }
            case PICKUP_HALF -> {
                if (clickedSlot == null || event.getCurrentItem().getAmount() > 1)
                    return;
                onEquipChange(p, EquipmentChangeEvent.EquipMethod.INVENTORY_PICKUP, clickedSlot, event.getCurrentItem(), null);
            }
            case PICKUP_ONE -> {
                if (clickedSlot == null || event.getCurrentItem().getAmount() != 1)
                    return;
                onEquipChange(p, EquipmentChangeEvent.EquipMethod.INVENTORY_PICKUP, clickedSlot, event.getCurrentItem(), null);
            }
            case PLACE_ALL, PLACE_ONE, PLACE_SOME -> {
                if (clickedSlot == null)
                    return;
                if (ItemUtils.isAirOrNull(event.getCurrentItem()))
                    onEquipChange(p, EquipmentChangeEvent.EquipMethod.INVENTORY_PLACE, clickedSlot, null, event.getCursor());
            }
            case SWAP_WITH_CURSOR -> {
                if (clickedSlot == null)
                    return;
                if (isSimilarIgnoreDamage(event.getCurrentItem(), event.getCursor()))
                    return;
                onEquipChange(p, EquipmentChangeEvent.EquipMethod.INVENTORY_SWAP_WITH_CURSOR, clickedSlot, event.getCurrentItem(),
                        event.getCursor());
            }
            case HOTBAR_SWAP -> {
                ItemStack to = event.getHotbarButton() == -1 ? p.getInventory().getItemInOffHand() : p.getInventory().getItem(event.getHotbarButton());
                if (isSimilarIgnoreDamage(event.getCurrentItem(), to))
                    return;
                if (clickedSlot != null)
                    onEquipChange(p, EquipmentChangeEvent.EquipMethod.INVENTORY_HOTBAR_SWAP, clickedSlot, event.getCurrentItem(), to);
                if (event.getHotbarButton() == -1)
                    onEquipChange(p, EquipmentChangeEvent.EquipMethod.INVENTORY_HOTBAR_SWAP, EquipmentSlot.OFF_HAND, to, event.getCurrentItem());
                else if (event.getHotbarButton() == p.getInventory().getHeldItemSlot())
                    onEquipChange(p, EquipmentChangeEvent.EquipMethod.INVENTORY_HOTBAR_SWAP, EquipmentSlot.HAND, to, event.getCurrentItem());
            }
            case MOVE_TO_OTHER_INVENTORY -> {
                EquipmentSlot slot = event.getView().getTopInventory().getType() == InventoryType.CRAFTING
                        ? guessDispenserSlotType(event.getCurrentItem())
                        : null;
                if (slot != null && ItemUtils.isAirOrNull(getEquip(p, slot)))
                    onEquipChange(p, EquipmentChangeEvent.EquipMethod.INVENTORY_MOVE_TO_OTHER_INVENTORY, slot, null, event.getCurrentItem());
                if (clickedSlot == null || clickedSlot == EquipmentSlot.HAND)
                    new SlotCheck(p, EquipmentChangeEvent.EquipMethod.INVENTORY_MOVE_TO_OTHER_INVENTORY, EquipmentSlot.HAND)
                            .runTaskLater(ItemTag.get(), 1L);
                else
                    new SlotCheck(p, EquipmentChangeEvent.EquipMethod.INVENTORY_MOVE_TO_OTHER_INVENTORY, clickedSlot).runTaskLater(ItemTag.get(),
                            1L);
            }
            case COLLECT_TO_CURSOR -> {
                ArrayList<EquipmentSlot> slots = new ArrayList<>();
                if (event.getView().getTopInventory().getType() == InventoryType.CRAFTING)
                    for (EquipmentSlot slot : InventoryUtils.getPlayerEquipmentSlots()) {
                        if (event.getCursor().isSimilar(getEquip(p, slot)))
                            slots.add(slot);
                    }
                else if (event.getCursor().isSimilar(getEquip(p, EquipmentSlot.HAND)))
                    slots.add(EquipmentSlot.HAND);
                if (!slots.isEmpty())
                    new SlotCheck(p, EquipmentChangeEvent.EquipMethod.INVENTORY_COLLECT_TO_CURSOR, slots).runTaskLater(ItemTag.get(), 1L);
            }
            case PICKUP_SOME, UNKNOWN -> {
                return; // ???
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void event(PlayerSwapHandItemsEvent event) {
        if (event.getPlayer().hasMetadata("NPC"))
            return;
        if (isSimilarIgnoreDamage(event.getMainHandItem(), event.getOffHandItem()))
            return;
        onEquipChange(event.getPlayer(), EquipmentChangeEvent.EquipMethod.SWAP_HANDS_ITEM, EquipmentSlot.HAND, event.getOffHandItem(),
                event.getMainHandItem());
        onEquipChange(event.getPlayer(), EquipmentChangeEvent.EquipMethod.SWAP_HANDS_ITEM, EquipmentSlot.OFF_HAND, event.getMainHandItem(),
                event.getOffHandItem());
    }

    @EventHandler
    private void event(BlockDispenseArmorEvent event) {
        if (!(event.getTargetEntity() instanceof Player))
            return;
        if (event.getTargetEntity().hasMetadata("NPC"))
            return;
        EquipmentSlot slot = guessDispenserSlotType(event.getItem());
        if (slot == null)
            throw new IllegalStateException(event.getItem().toString());
        onEquipChange((Player) event.getTargetEntity(), EquipmentChangeEvent.EquipMethod.DISPENSER, slot, null, event.getItem());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void event(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player p))
            return;
        if (event.getEntity().hasMetadata("NPC"))
            return;

        if (!ItemUtils.isAirOrNull(getEquip(p, EquipmentSlot.HAND)))
            return;
        for (int i = 0; i < p.getInventory().getHeldItemSlot(); i++)
            if (ItemUtils.isAirOrNull(p.getInventory().getItem(i)))
                return;
        new SlotCheck(p, EquipmentChangeEvent.EquipMethod.PICKUP, EquipmentSlot.HAND).runTaskLater(ItemTag.get(), 1L);
    }

    public boolean isSimilarIgnoreDamage(ItemStack item, ItemStack item2) {
        if (ItemUtils.isAirOrNull(item))
            return ItemUtils.isAirOrNull(item2);
        if (ItemUtils.isAirOrNull(item2))
            return false;
        if (item.isSimilar(item2))
            return true;
        if (item.getType() != item2.getType())
            return false;
        ItemMeta meta1 = item.getItemMeta();
        if (!(meta1 instanceof Damageable))
            return false;
        ItemMeta meta2 = item2.getItemMeta();
        if (meta1.isUnbreakable() || meta2.isUnbreakable())
            return false;
        ((Damageable) meta2).setDamage(((Damageable) meta1).getDamage());
        return meta1.equals(meta2);
    }
}
package emanondev.itemtag;

import emanondev.itemedit.utility.InventoryUtils;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class ItemTagUtility {

    private static final EnumSet<EquipmentSlot> playerEquipmentSlots = loadPlayerEquipmentSlot();

    private static EnumSet<EquipmentSlot> loadPlayerEquipmentSlot() {
        EnumSet<EquipmentSlot> slots = EnumSet.noneOf(EquipmentSlot.class);
        slots.add(EquipmentSlot.HEAD);
        slots.add(EquipmentSlot.CHEST);
        slots.add(EquipmentSlot.LEGS);
        slots.add(EquipmentSlot.FEET);
        slots.add(EquipmentSlot.HAND);
        try {
            slots.add(EquipmentSlot.valueOf("OFF_HAND"));
        } catch (Throwable ignored) {
            //1.8
        }
        return slots;
    }

    /**
     * @see InventoryUtils#getPlayerEquipmentSlots()
     */
    @Deprecated
    public static Set<EquipmentSlot> getPlayerEquipmentSlots() {
        return Collections.unmodifiableSet(playerEquipmentSlots);
    }
}

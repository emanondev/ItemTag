package emanondev.itemtag.equipmentchange;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * EquipmentChangeEvent purpose is to provide a read only / notify event for equipment changes<br>
 * The event may be called while the event of changing the equipment is still going on or a few ticks later<br>
 * <br>
 * The equipment method may not be always accurate<br>
 * <br>
 * If you wish to cancel the event it's suggested to handle the player equipment one tick later
 */
public class EquipmentChangeEvent extends PlayerEvent {
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private final ItemStack from;
    private final ItemStack to;
    private final EquipmentSlot slot;
    private final EquipMethod method;

    public EquipmentChangeEvent(Player who, EquipMethod method, EquipmentSlot slot, ItemStack from, ItemStack to) {
        super(who);
        this.from = from;
        this.to = to;
        this.slot = slot;
        this.method = method;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    /**
     * Changes to the item may have unexpected results
     *
     * @return the item before changing
     */
    public ItemStack getFrom() {
        return from;
    }

    /**
     * Changes to the item may have unexpected results
     *
     * @return the item after changing
     */
    public ItemStack getTo() {
        return to;
    }

    /**
     * @return where the change is happening
     */
    public EquipmentSlot getSlotType() {
        return slot;
    }

    /**
     * The equipment method may not be always accurate
     *
     * @return why the event occurred
     */
    public EquipMethod getMethod() {
        return method;
    }

    public enum EquipMethod {// These have got to be the worst documentations ever.
        /**
         * When you shift click an armor piece to equip or unequip
         */
        SHIFT_CLICK,
        /**
         * When you drag and drop the item to equip or unequip
         */
        INVENTORY_DRAG,
        /**
         * When in range of a dispenser that shoots an armor piece to equip.<br>
         * Requires the spigot version to have
         * {@link org.bukkit.event.block.BlockDispenseArmorEvent} implemented. Which is
         * 1.13.1+.
         */
        DISPENSER,
        /**
         * When an armor piece is removed due to it losing all durability.
         */
        BROKE,
        /**
         * When you die causing all armor to unequip even if keep inventory is true
         *
         * @see #RESPAWN
         */
        DEATH,
        /**
         * When player change world, usually caused by plugin which handles per-world-inventory
         */
        PLUGIN_WORLD_CHANGE,

        COMMAND,

        /**
         * When an item is consumed as result of a click
         * opposed to USE which is left click
         *
         * @see #USE
         */
        RIGHT_CLICK,

        /**
         * When player swap hands
         */
        SWAP_HANDS_ITEM,

        /**
         * When you respawn keepinventory was set to true
         *
         * @see #RESPAWN
         */
        RESPAWN,
        /**
         * When player change the slot of his main hand
         */
        HOTBAR_HAND_CHANGE,
        /**
         * When player grab an item from floor to his hand
         *
         * @see #RESPAWN
         */
        PICKUP,
        /**
         * When player consume an item, like food and drinks
         *
         * @see #RESPAWN
         */
        CONSUME,
        /**
         * When player drops an item
         *
         * @see #RESPAWN
         */
        DROP,
        /**
         * When player consume color in his hand to dye a sheep
         *
         * @see #RESPAWN
         */
        SHEEP_COLOR,
        /**
         * When player consume a nametag on an entity
         *
         * @see #RESPAWN
         */
        NAMETAG_APPLY,
        /**
         * When equipment change reason is unknow (most likely done by plugins)
         *
         * @see #RESPAWN
         */
        UNKNOWN,
        /**
         * When player drops equipment with inventory open
         */
        INVENTORY_DROP,
        /**
         * When player take equipment with his cursor
         */
        INVENTORY_PICKUP,
        /**
         * When player place equipment with his cursor
         */
        INVENTORY_PLACE,
        /**
         * When player swap an equipment using hotbars
         */
        INVENTORY_HOTBAR_SWAP,
        /**
         * When player swap hands
         */
        INVENTORY_MOVE_TO_OTHER_INVENTORY,
        /**
         * When player take equipment with a double click
         */
        INVENTORY_COLLECT_TO_CURSOR,
        /**
         * When player swap equipment with his cursor location
         */
        INVENTORY_SWAP_WITH_CURSOR,
        /**
         * When an item is consumed as result of an action like placing a block
         */
        USE,
        /**
         * When an item taken or placed on an armorstand
         */
        ARMOR_STAND_MANIPULATE,
        /**
         * When an item consumed as result of an interaction with an entity
         */
        USE_ON_ENTITY,
        /**
         * When player quit the game all his equipment is considered as unequipped
         */
        QUIT,
        /**
         * When player join the game all his equipment is considered as equipped
         */
        JOIN
    }
}

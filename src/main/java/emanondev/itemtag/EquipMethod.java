package emanondev.itemtag;

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
	 * When you die causing all armor to unequip
	 */
	DEATH, PLUGIN_WORLD_CHANGE, COMMAND, RIGHT_CLICK, SWAP_HANDS_ITEM, RESPAWN, HOTBAR_HAND_CHANGE, PICKUP, CONSUME,
	DROP, SHEEP_COLOR, NAMETAG_APPLY, UNKNOWN, INVENTORY_DROP, INVENTORY_PICKUP, INVENTORY_PLACE,
	INVENTORY_HOTBAR_SWAP, INVENTORY_MOVE_TO_OTHER_INVENTORY, INVENTORY_COLLECT_TO_CURSOR,
	INVENTORY_SWAP_WITH_CURSOR, USE, ARMOR_STAND_MANIPULATE, USE_ON_ENTITY, QUIT, JOIN
}
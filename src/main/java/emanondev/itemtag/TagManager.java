package emanondev.itemtag;

import org.bukkit.inventory.ItemStack;

/**
 * @see TagItem
 */
@Deprecated
interface TagManager {

    boolean hasBooleanTag(String key, ItemStack item);

    boolean hasIntegerTag(String key, ItemStack item);

    boolean hasDoubleTag(String key, ItemStack item);

    boolean hasStringTag(String key, ItemStack item);

    ItemStack removeTag(String key, ItemStack item);

    ItemStack setTag(String key, ItemStack item, boolean value);

    ItemStack setTag(String key, ItemStack item, String value);

    ItemStack setTag(String key, ItemStack item, int value);

    ItemStack setTag(String key, ItemStack item, double value);

    Boolean getBoolean(String key, ItemStack item);

    String getString(String key, ItemStack item);

    Integer getInteger(String key, ItemStack item);

    Double getDouble(String key, ItemStack item);

}

package emanondev.itemtag;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @see NBTAPITagItem
 */
@Deprecated
public class NBTAPITagManager implements TagManager {


    public boolean hasTag(String key, ItemStack item) {
        if (item == null || item.getType() == Material.AIR)
            return false;
        return new NBTItem(item).hasKey(key);
    }

    @Override
    public ItemStack removeTag(String key, ItemStack item) {
        NBTItem nbti = new NBTItem(item);
        if (nbti.hasKey(key)) {
            nbti.removeKey(key);
            return nbti.getItem();
        }
        return item;
    }

    @Override
    public ItemStack setTag(String key, ItemStack item, boolean value) {
        NBTItem nbti = new NBTItem(item);
        nbti.setInteger(key, value ? 1 : 0);
        return nbti.getItem();
    }

    @Override
    public ItemStack setTag(String key, ItemStack item, String value) {
        NBTItem nbti = new NBTItem(item);
        nbti.setString(key, value);
        return nbti.getItem();
    }

    @Override
    public ItemStack setTag(String key, ItemStack item, int value) {
        NBTItem nbti = new NBTItem(item);
        nbti.setInteger(key, value);
        return nbti.getItem();
    }

    @Override
    public ItemStack setTag(String key, ItemStack item, double value) {
        NBTItem nbti = new NBTItem(item);
        nbti.setDouble(key, value);
        return nbti.getItem();
    }

    @Override
    public Boolean getBoolean(String key, ItemStack item) {
        return new NBTItem(item).getInteger(key) != 0;
    }

    @Override
    public String getString(String key, ItemStack item) {
        return new NBTItem(item).getString(key);
    }

    @Override
    public Integer getInteger(String key, ItemStack item) {
        return new NBTItem(item).getInteger(key);
    }

    @Override
    public Double getDouble(String key, ItemStack item) {
        return new NBTItem(item).getDouble(key);
    }

    @Override
    public boolean hasBooleanTag(String key, ItemStack item) {
        return hasTag(key, item);
    }

    @Override
    public boolean hasIntegerTag(String key, ItemStack item) {
        return hasTag(key, item);
    }

    @Override
    public boolean hasDoubleTag(String key, ItemStack item) {
        return hasTag(key, item);
    }

    @Override
    public boolean hasStringTag(String key, ItemStack item) {
        return hasTag(key, item);
    }

}

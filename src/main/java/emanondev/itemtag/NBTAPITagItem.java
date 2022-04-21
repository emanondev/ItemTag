package emanondev.itemtag;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class NBTAPITagItem implements TagItem {

    private final ItemStack item;
    private NBTItem nbtItem = null;

    public NBTItem getNbtItem() {
        if (nbtItem == null)
            nbtItem = new NBTItem(item, true);
        return nbtItem;
    }

    public NBTAPITagItem(@Nullable ItemStack item) {
        this.item = item;
    }

    public boolean hasTag(String key) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta())
            return false;
        return getNbtItem().hasKey(key);
    }

    @Override
    public boolean hasBooleanTag(String key) {
        return hasTag(key);
    }

    @Override
    public boolean hasIntegerTag(String key) {
        return hasTag(key);
    }

    @Override
    public boolean hasDoubleTag(String key) {
        return hasTag(key);
    }

    @Override
    public boolean hasStringTag(String key) {
        return hasTag(key);
    }

    @Override
    public boolean hasStringListTag(String key) {
        return hasTag(key);
    }

    @Override
    public void removeTag(String key) {
        getNbtItem().removeKey(key);
    }

    @Override
    public void setTag(String key, boolean value) {
        getNbtItem().setBoolean(key, value);
    }

    @Override
    public void setTag(String key, String value) {
        getNbtItem().setString(key, value);
    }

    @Override
    public void setTag(String key, int value) {
        getNbtItem().setInteger(key, value);
    }

    @Override
    public void setTag(String key, double value) {
        getNbtItem().setDouble(key, value);
    }

    @Override
    public Boolean getBoolean(String key) {
        return getNbtItem().getBoolean(key);
    }

    @Override
    public String getString(String key) {
        return getNbtItem().getString(key);
    }

    @Override
    public Integer getInteger(String key) {
        return getNbtItem().getInteger(key);
    }

    @Override
    public Double getDouble(String key) {
        return getNbtItem().getDouble(key);
    }

    @Override
    public boolean isValid() {
        return item != null && item.getType() != Material.AIR;
    }
}

package emanondev.itemtag;

import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NBTAPITagItem implements TagItem {

    @Getter
    private final ItemStack item;
    private NBTItem nbtItem = null;

    public NBTAPITagItem(@Nullable ItemStack item) {
        this.item = item;
    }

    public NBTItem getNbtItem() {
        if (nbtItem == null)
            nbtItem = new NBTItem(item, true);
        return nbtItem;
    }

    public boolean hasTag(@NotNull String key) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta())
            return false;
        return getNbtItem().hasKey(key);
    }

    @Override
    public boolean hasBooleanTag(@NotNull String key) {
        return hasTag(key);
    }

    @Override
    public boolean hasIntegerTag(@NotNull String key) {
        return hasTag(key);
    }

    @Override
    public boolean hasDoubleTag(@NotNull String key) {
        return hasTag(key);
    }

    @Override
    public boolean hasStringTag(@NotNull String key) {
        return hasTag(key);
    }

    @Override
    public boolean hasStringListTag(@NotNull String key) {
        return hasTag(key);
    }

    @Override
    public void removeTag(@NotNull String key) {
        getNbtItem().removeKey(key);
    }

    @Override
    public void setTag(@NotNull String key, boolean value) {
        getNbtItem().setBoolean(key, value);
    }

    @Override
    public void setTag(@NotNull String key, @Nullable String value) {
        getNbtItem().setString(key, value);
    }

    @Override
    public void setTag(@NotNull String key, int value) {
        getNbtItem().setInteger(key, value);
    }

    @Override
    public void setTag(@NotNull String key, double value) {
        getNbtItem().setDouble(key, value);
    }

    @Override
    @Nullable
    public Boolean getBoolean(@NotNull String key) {
        return getNbtItem().getBoolean(key);
    }

    @Override
    @Nullable
    public String getString(@NotNull String key) {
        return getNbtItem().getString(key);
    }

    @Override
    @Nullable
    public Integer getInteger(@NotNull String key) {
        return getNbtItem().getInteger(key);
    }

    @Override
    @Nullable
    public Double getDouble(@NotNull String key) {
        return getNbtItem().getDouble(key);
    }

    @Override
    public boolean isValid() {
        return item != null && item.getType() != Material.AIR;
    }

}

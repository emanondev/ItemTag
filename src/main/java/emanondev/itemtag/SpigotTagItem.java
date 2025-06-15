package emanondev.itemtag;

import emanondev.itemedit.utility.ItemUtils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class SpigotTagItem implements TagItem {

    private static final HashMap<String, NamespacedKey> keys = new HashMap<String, NamespacedKey>() {
        @Override
        public NamespacedKey get(Object key) {
            NamespacedKey keyN = super.get(key);
            if (keyN != null)
                return keyN;
            String[] args = ((String) key).split(":");
            keyN = new NamespacedKey(args[0], args[1]);
            this.put((String) key, keyN);
            return keyN;
        }
    };

    @Getter
    private final ItemStack item;
    private ItemMeta meta = null;
    private PersistentDataContainer data = null;

    public SpigotTagItem(@Nullable ItemStack item) {
        this.item = item;
    }

    private PersistentDataContainer getData() {
        if (data == null) {
            this.meta = ItemUtils.getMeta(this.item);
            this.data = this.meta.getPersistentDataContainer();
        }
        return data;
    }

    public <T, Z> boolean hasTag(@NotNull String key, @NotNull PersistentDataType<T, Z> type) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta())
            return false;
        return getData().get(keys.get(key), type) != null;
    }

    @Override
    public boolean hasBooleanTag(@NotNull String key) {
        return hasTag(key, PersistentDataType.INTEGER);
    }

    @Override
    public boolean hasIntegerTag(@NotNull String key) {
        return hasTag(key, PersistentDataType.INTEGER);
    }

    @Override
    public boolean hasDoubleTag(@NotNull String key) {
        return hasTag(key, PersistentDataType.DOUBLE);
    }

    @Override
    public boolean hasStringTag(@NotNull String key) {
        return hasTag(key, PersistentDataType.STRING);
    }

    @Override
    public boolean hasStringListTag(@NotNull String key) {
        return hasTag(key, PersistentDataType.STRING);
    }

    @Override
    public void removeTag(@NotNull String key) {
        getData().remove(keys.get(key));
        item.setItemMeta(meta);
    }

    @Override
    public void setTag(@NotNull String key, boolean value) {
        setTag(key, value ? 1 : 0);
    }

    @Override
    public void setTag(@NotNull String key, @Nullable String value) {
        if (value == null)
            removeTag(key);
        else {
            getData().set(keys.get(key), PersistentDataType.STRING, value);
            item.setItemMeta(meta);
        }
    }


    @Override
    public void setTag(@NotNull String key, int value) {
        getData().set(keys.get(key), PersistentDataType.INTEGER, value);
        item.setItemMeta(meta);
    }

    @Override
    public void setTag(@NotNull String key, double value) {
        getData().set(keys.get(key), PersistentDataType.DOUBLE, value);
        item.setItemMeta(meta);
    }

    @Override
    @Nullable
    public Boolean getBoolean(@NotNull String key) {
        Integer value = getInteger(key);
        return value == null ? null : value != 0;
    }

    @Override
    @Nullable
    public String getString(@NotNull String key) {
        return getData().get(keys.get(key), PersistentDataType.STRING);
    }

    @Override
    @Nullable
    public Integer getInteger(@NotNull String key) {
        return getData().get(keys.get(key), PersistentDataType.INTEGER);
    }

    @Override
    @Nullable
    public Double getDouble(@NotNull String key) {
        return getData().get(keys.get(key), PersistentDataType.DOUBLE);
    }

    @Override
    public boolean isValid() {
        return item != null && item.getType() != Material.AIR;
    }

}

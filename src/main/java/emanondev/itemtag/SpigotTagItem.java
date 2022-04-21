package emanondev.itemtag;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.WeakHashMap;

public class SpigotTagItem implements TagItem {

    private final WeakHashMap<String, NamespacedKey> keys = new WeakHashMap<String, NamespacedKey>() {
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

    private final ItemStack item;
    private ItemMeta meta = null;
    private PersistentDataContainer data = null;

    private PersistentDataContainer getData() {
        if (data == null) {
            this.meta = this.item.getItemMeta();
            this.data = this.meta.getPersistentDataContainer();
        }
        return data;
    }

    public SpigotTagItem(ItemStack item) {
        //if (item == null || item.getType() == Material.AIR)
        //    throw new IllegalArgumentException();
        this.item = item;
    }

    public <T, Z> boolean hasTag(String key, PersistentDataType<T, Z> type) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta())
            return false;
        return getData().get(keys.get(key), type) != null;
    }

    @Override
    public boolean hasBooleanTag(String key) {
        return hasTag(key, PersistentDataType.INTEGER);
    }

    @Override
    public boolean hasIntegerTag(String key) {
        return hasTag(key, PersistentDataType.INTEGER);
    }

    @Override
    public boolean hasDoubleTag(String key) {
        return hasTag(key, PersistentDataType.DOUBLE);
    }

    @Override
    public boolean hasStringTag(String key) {
        return hasTag(key, PersistentDataType.STRING);
    }

    @Override
    public boolean hasStringListTag(String key) {
        return hasTag(key, PersistentDataType.STRING);
    }

    @Override
    public void removeTag(String key) {
        getData().remove(keys.get(key));
        item.setItemMeta(meta);
    }

    @Override
    public void setTag(String key, boolean value) {
        setTag(key, value ? 1 : 0);
    }

    @Override
    public void setTag(String key, String value) {
        if (value == null)
            removeTag(key);
        else {
            getData().set(keys.get(key), PersistentDataType.STRING, value);
            item.setItemMeta(meta);
        }
    }


    @Override
    public void setTag(String key, int value) {
        getData().set(keys.get(key), PersistentDataType.INTEGER, value);
        item.setItemMeta(meta);
    }

    @Override
    public void setTag(String key, double value) {
        getData().set(keys.get(key), PersistentDataType.DOUBLE, value);
        item.setItemMeta(meta);
    }

    @Override
    public Boolean getBoolean(String key) {
        Integer value = getInteger(key);
        return value == null ? null : value != 0;
    }

    @Override
    public String getString(String key) {
        return getData().get(keys.get(key), PersistentDataType.STRING);
    }

    @Override
    public Integer getInteger(String key) {
        return getData().get(keys.get(key), PersistentDataType.INTEGER);
    }

    @Override
    public Double getDouble(String key) {
        return getData().get(keys.get(key), PersistentDataType.DOUBLE);
    }

    @Override
    public boolean isValid() {
        return item != null && item.getType() != Material.AIR;
    }
}

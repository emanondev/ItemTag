package emanondev.itemtag;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.WeakHashMap;

/**
 * @see SpigotTagItem
 */
@Deprecated
public class SpigotTagManager implements TagManager {

    private final WeakHashMap<String, NamespacedKey> keys = new WeakHashMap<>();

    @Override
    public boolean hasBooleanTag(String key, ItemStack item) {
        return hasTag(key, item, PersistentDataType.INTEGER);
    }

    @Override
    public boolean hasIntegerTag(String key, ItemStack item) {
        return hasTag(key, item, PersistentDataType.INTEGER);
    }

    @Override
    public boolean hasDoubleTag(String key, ItemStack item) {
        return hasTag(key, item, PersistentDataType.DOUBLE);
    }

    @Override
    public boolean hasStringTag(String key, ItemStack item) {
        return hasTag(key, item, PersistentDataType.STRING);
    }

    @Override
    public ItemStack setTag(String key, ItemStack item, boolean value) {
        return setTag(key, item, PersistentDataType.INTEGER, value ? 1 : 0);
    }

    @Override
    public ItemStack setTag(String key, ItemStack item, String value) {
        return setTag(key, item, PersistentDataType.STRING, value);
    }

    @Override
    public ItemStack setTag(String key, ItemStack item, int value) {
        return setTag(key, item, PersistentDataType.INTEGER, value);
    }

    @Override
    public ItemStack setTag(String key, ItemStack item, double value) {
        return setTag(key, item, PersistentDataType.DOUBLE, value);
    }

    @Override
    public Boolean getBoolean(String key, ItemStack item) {
        Integer value = get(key, item, PersistentDataType.INTEGER);
        return value == null ? null : (value != 0);
    }

    @Override
    public String getString(String key, ItemStack item) {
        return get(key, item, PersistentDataType.STRING);
    }

    @Override
    public Integer getInteger(String key, ItemStack item) {
        return get(key, item, PersistentDataType.INTEGER);
    }

    @Override
    public Double getDouble(String key, ItemStack item) {
        return get(key, item, PersistentDataType.DOUBLE);
    }

    @Override
    public ItemStack removeTag(String key, ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        putKeyIfAbsent(key);
        meta.getPersistentDataContainer().remove(keys.get(key));
        item.setItemMeta(meta);
        return item;
    }

    public <T, Z> boolean hasTag(String key, ItemStack item, PersistentDataType<T, Z> type) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta())
            return false;
        putKeyIfAbsent(key);
        return item.getItemMeta().getPersistentDataContainer().get(keys.get(key), type) != null;
    }

    public <Z, T> ItemStack setTag(String key, ItemStack item, PersistentDataType<Z, T> type, T value) {
        ItemMeta meta = item.getItemMeta();
        putKeyIfAbsent(key);
        meta.getPersistentDataContainer().set(keys.get(key), type, value);
        item.setItemMeta(meta);
        return item;
    }

    public <Z, T> T get(String key, ItemStack item, PersistentDataType<Z, T> type) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta())
            return null;
        putKeyIfAbsent(key);
        return item.getItemMeta().getPersistentDataContainer().get(keys.get(key), type);
    }

    private void putKeyIfAbsent(String key) {
        if (!keys.containsKey(key)) {
            String[] args = key.split(":");
            keys.put(key, new NamespacedKey(args[0], args[1]));
        }
    }

}

package emanondev.itemtag;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public interface TagItem {

    /**
     * @param key with format similar to NamespacedKey "pluginname:truekey"
     * @return true if contains a value
     */
    boolean hasBooleanTag(@NotNull String key);

    /**
     * @param key with format similar to NamespacedKey "pluginname:truekey"
     * @return true if contains a value
     */
    boolean hasIntegerTag(@NotNull String key);

    /**
     * @param key with format similar to NamespacedKey "pluginname:truekey"
     * @return true if contains a value
     */
    boolean hasDoubleTag(@NotNull String key);

    /**
     * @param key with format similar to NamespacedKey "pluginname:truekey"
     * @return true if contains a value
     */
    boolean hasStringTag(@NotNull String key);

    /**
     * @param key with format similar to NamespacedKey "pluginname:truekey"
     * @return true if contains a value
     */
    boolean hasStringListTag(@NotNull String key);

    /**
     * @param key with format similar to NamespacedKey "pluginname:truekey"
     */
    void removeTag(@NotNull String key);

    /**
     * @param key   with format similar to NamespacedKey "pluginname:truekey"
     * @param value value to set, if null is removed
     */
    void setTag(@NotNull String key, boolean value);

    /**
     * @param key   with format similar to NamespacedKey "pluginname:truekey"
     * @param value value to set, if null is removed
     */
    void setTag(@NotNull String key, @Nullable String value);

    /**
     * @param key   with format similar to NamespacedKey "pluginname:truekey"
     * @param value value to set, if null is removed
     */
    default void setTag(@NotNull String key, @Nullable List<String> value) {
        if (value == null)
            removeTag(key);
        else
            setTag(key, String.join("%%;%%", value));
    }

    /**
     * @param key   with format similar to NamespacedKey "pluginname:truekey"
     * @param value value to set, if null is removed
     */
    void setTag(@NotNull String key, int value);

    /**
     * @param key   with format similar to NamespacedKey "pluginname:truekey"
     * @param value value to set, if null is removed
     */
    void setTag(@NotNull String key, double value);

    /**
     * @param key with format similar to NamespacedKey "pluginname:truekey"
     */
    @Nullable
    Boolean getBoolean(@NotNull String key);

    /**
     * @param key with format similar to NamespacedKey "pluginname:truekey"
     */
    @Nullable
    String getString(@NotNull String key);

    /**
     * @param key with format similar to NamespacedKey "pluginname:truekey"
     */
    @Nullable
    default List<String> getStringList(@NotNull String key) {
        String value = getString(key);
        return value == null ? null : Arrays.asList(value.split("%%;%%"));
    }

    /**
     * @param key with format similar to NamespacedKey "pluginname:truekey"
     */
    @Nullable
    Integer getInteger(@NotNull String key);

    /**
     * @param key with format similar to NamespacedKey "pluginname:truekey"
     */
    @Nullable
    Double getDouble(@NotNull String key);

    /**
     * @return true if it's possible to apply tags to item
     */
    boolean isValid();

    ItemStack getItem();
}

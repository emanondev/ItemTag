package emanondev.itemtag;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public interface TagItem {

    /**
     * @param key with format similar to NamespacedKey "pluginname:truekey"
     * @return true if contains a value
     */
    boolean hasBooleanTag(String key);

    /**
     * @param key with format similar to NamespacedKey "pluginname:truekey"
     * @return true if contains a value
     */
    boolean hasIntegerTag(String key);

    /**
     * @param key with format similar to NamespacedKey "pluginname:truekey"
     * @return true if contains a value
     */
    boolean hasDoubleTag(String key);

    /**
     * @param key with format similar to NamespacedKey "pluginname:truekey"
     * @return true if contains a value
     */
    boolean hasStringTag(String key);

    /**
     * @param key with format similar to NamespacedKey "pluginname:truekey"
     * @return true if contains a value
     */
    boolean hasStringListTag(String key);

    /**
     * @param key with format similar to NamespacedKey "pluginname:truekey"
     */
    void removeTag(String key);

    /**
     * @param key   with format similar to NamespacedKey "pluginname:truekey"
     * @param value value to set, if null is removed
     */
    void setTag(String key, boolean value);

    /**
     * @param key   with format similar to NamespacedKey "pluginname:truekey"
     * @param value value to set, if null is removed
     */
    void setTag(String key, String value);

    /**
     * @param key   with format similar to NamespacedKey "pluginname:truekey"
     * @param value value to set, if null is removed
     */
    default void setTag(String key, List<String> value) {
        if (value == null)
            removeTag(key);
        else
            setTag(key, String.join("%%;%%", value));
    }

    /**
     * @param key   with format similar to NamespacedKey "pluginname:truekey"
     * @param value value to set, if null is removed
     */
    void setTag(String key, int value);

    /**
     * @param key   with format similar to NamespacedKey "pluginname:truekey"
     * @param value value to set, if null is removed
     */
    void setTag(String key, double value);

    /**
     * @param key with format similar to NamespacedKey "pluginname:truekey"
     */
    Boolean getBoolean(String key);

    /**
     * @param key with format similar to NamespacedKey "pluginname:truekey"
     */
    String getString(String key);

    /**
     * @param key with format similar to NamespacedKey "pluginname:truekey"
     */
    default @Nullable List<String> getStringList(String key) {
        if (getString(key) == null)
            return null;
        return Arrays.asList(getString(key).split("%%;%%"));
    }

    /**
     * @param key with format similar to NamespacedKey "pluginname:truekey"
     */
    Integer getInteger(String key);

    /**
     * @param key with format similar to NamespacedKey "pluginname:truekey"
     */
    Double getDouble(String key);

    /**
     * @return true if it's possible to apply tags to item
     */
    boolean isValid();
}

package emanondev.itemtag;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public interface TagItem {

    /**
     * Checks if the item contains a boolean tag with the specified key.
     *
     * @param key the key in the format "pluginname:key"
     * @return true if the tag exists, false otherwise
     */
    boolean hasBooleanTag(@NotNull String key);

    /**
     * Checks if the item contains an integer tag with the specified key.
     *
     * @param key the key in the format "pluginname:key"
     * @return true if the tag exists, false otherwise
     */
    boolean hasIntegerTag(@NotNull String key);

    /**
     * Checks if the item contains a double tag with the specified key.
     *
     * @param key the key in the format "pluginname:key"
     * @return true if the tag exists, false otherwise
     */
    boolean hasDoubleTag(@NotNull String key);

    /**
     * Checks if the item contains a string tag with the specified key.
     *
     * @param key the key in the format "pluginname:key"
     * @return true if the tag exists, false otherwise
     */
    boolean hasStringTag(@NotNull String key);

    /**
     * Checks if the item contains a string list tag with the specified key.
     *
     * @param key the key in the format "pluginname:key"
     * @return true if the tag exists, false otherwise
     */
    boolean hasStringListTag(@NotNull String key);

    /**
     * Removes a tag from the item using the specified key.
     *
     * @param key the key in the format "pluginname:key"
     */
    void removeTag(@NotNull String key);

    /**
     * Sets a boolean tag on the item.
     *
     * @param key   the key in the format "pluginname:key"
     * @param value the boolean value to set
     */
    void setTag(@NotNull String key, boolean value);

    /**
     * Sets a string tag on the item.
     *
     * @param key   the key in the format "pluginname:key"
     * @param value the string value to set; if null, the tag is removed
     */
    void setTag(@NotNull String key, @Nullable String value);

    /**
     * Sets a list of strings as a tag on the item. The list is serialized into a single string.
     *
     * @param key   the key in the format "pluginname:key"
     * @param value the list of strings to set; if null, the tag is removed
     */
    default void setTag(@NotNull String key, @Nullable List<String> value) {
        if (value == null)
            removeTag(key);
        else
            setTag(key, String.join("%%;%%", value));
    }

    /**
     * Sets an integer tag on the item.
     *
     * @param key   the key in the format "pluginname:key"
     * @param value the integer value to set
     */
    void setTag(@NotNull String key, int value);

    /**
     * Sets a double tag on the item.
     *
     * @param key   the key in the format "pluginname:key"
     * @param value the double value to set
     */
    void setTag(@NotNull String key, double value);

    /**
     * Retrieves a boolean tag from the item.
     *
     * @param key the key in the format "pluginname:key"
     * @return the boolean value of the tag, or null if the tag does not exist
     */
    @Nullable
    Boolean getBoolean(@NotNull String key);

    /**
     * Retrieves a string tag from the item.
     *
     * @param key the key in the format "pluginname:key"
     * @return the string value of the tag, or null if the tag does not exist
     */
    @Nullable
    String getString(@NotNull String key);

    /**
     * Retrieves a list of strings from the item. The list is deserialized from a single string.
     *
     * @param key the key in the format "pluginname:key"
     * @return the list of strings, or null if the tag does not exist
     */
    @Nullable
    default List<String> getStringList(@NotNull String key) {
        String value = getString(key);
        return value == null ? null : Arrays.asList(value.split("%%;%%"));
    }

    /**
     * Retrieves an integer tag from the item.
     *
     * @param key the key in the format "pluginname:key"
     * @return the integer value of the tag, or null if the tag does not exist
     */
    @Nullable
    Integer getInteger(@NotNull String key);

    /**
     * Retrieves an integer tag from the item, or returns a default value if the tag does not exist.
     *
     * @param key          the key in the format "pluginname:key"
     * @param defaultValue the default value to return if the tag does not exist
     * @return the integer value of the tag, or the default value
     */
    @Nullable
    @Contract("_,!null->!null")
    default Integer getInteger(@NotNull String key, @Nullable Integer defaultValue) {
        Integer value = getInteger(key);
        return value == null ? defaultValue : value;
    }

    /**
     * Retrieves a double tag from the item.
     *
     * @param key the key in the format "pluginname:key"
     * @return the double value of the tag, or null if the tag does not exist
     */
    @Nullable
    Double getDouble(@NotNull String key);

    /**
     * Retrieves a boolean tag from the item, or returns a default value if the tag does not exist.
     *
     * @param key          the key in the format "pluginname:key"
     * @param defaultValue the default value to return if the tag does not exist
     * @return the boolean value of the tag, or the default value
     */
    @Nullable
    @Contract("_,!null->!null")
    default Boolean getBoolean(@NotNull String key, @Nullable Boolean defaultValue) {
        Boolean value = getBoolean(key);
        return value == null ? defaultValue : value;
    }

    /**
     * Retrieves a double tag from the item, or returns a default value if the tag does not exist.
     *
     * @param key          the key in the format "pluginname:key"
     * @param defaultValue the default value to return if the tag does not exist
     * @return the double value of the tag, or the default value
     */
    @Nullable
    @Contract("_,!null->!null")
    default Double getDouble(@NotNull String key, @Nullable Double defaultValue) {
        Double value = getDouble(key);
        return value == null ? defaultValue : value;
    }

    /**
     * Retrieves a string tag from the item, or returns a default value if the tag does not exist.
     *
     * @param key          the key in the format "pluginname:key"
     * @param defaultValue the default value to return if the tag does not exist
     * @return the string value of the tag, or the default value
     */
    @Nullable
    @Contract("_,!null->!null")
    default String getString(@NotNull String key, @Nullable String defaultValue) {
        String value = getString(key);
        return value == null ? defaultValue : value;
    }

    /**
     * Retrieves a list of strings from the item, or returns a default value if the tag does not exist.
     *
     * @param key          the key in the format "pluginname:key"
     * @param defaultValue the default value to return if the tag does not exist
     * @return the list of strings, or the default value
     */
    @Nullable
    @Contract("_,!null->!null")
    default List<String> getStringList(@NotNull String key, @Nullable List<String> defaultValue) {
        List<String> value = getStringList(key);
        return value == null ? defaultValue : value;
    }

    /**
     * Checks if the item is valid for tagging operations.
     *
     * @return true if the item is valid, false otherwise
     */
    boolean isValid();

    /**
     * Retrieves the underlying {@link ItemStack} associated with this item.
     *
     * @return the ItemStack instance
     */
    ItemStack getItem();
}

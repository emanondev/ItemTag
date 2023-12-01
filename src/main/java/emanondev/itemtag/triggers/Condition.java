package emanondev.itemtag.triggers;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class Condition {

    @Nullable
    private final ConditionType type;
    private final Map<String, Object> rawValues = new LinkedHashMap<>();

    public Condition(@NotNull Map<String, Object> values) {
        rawValues.putAll(values);
        type = ConditionManager.getConditionType((String) values.get("type"));
        //if (type==null)
        //    throw new IllegalArgumentException();
        //TODO debug
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(rawValues);
    }

    public <E extends Event> boolean isSatisfied(@NotNull E e,@NotNull  Player target) {
        if (type == null) {

            //TODO
            return true;
        }
        if (!e.getClass().isAssignableFrom(type.getEventType())) {
            //TODO
            return true;
        }
        return isReversed() != type.isSatisfied(this,e,target);
    }

    public boolean isReversed() {
        return (boolean) rawValues.getOrDefault("reversed", false);
    }

    public void setReversed(boolean value) {
        if (value)
            rawValues.put("reversed", true);
        else
            rawValues.remove("reversed");
    }

    public void setValue(@NotNull String key, @Nullable Object value) {
        if (value == null)
            rawValues.remove(key);
        else
            rawValues.put(key, value);
    }

    public <T, L extends T> @Nullable T getValue(@NotNull String key, @Nullable L def, @NotNull Class<T> clazz) {
        try {
            return (T) rawValues.getOrDefault(key, def);
        } catch (ClassCastException e) {
            e.printStackTrace();
            return def;
        }
    }
}

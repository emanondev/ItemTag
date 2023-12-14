package emanondev.itemtag.activity;

import emanondev.itemtag.ItemTag;
import emanondev.itemtag.activity.condition.HasUsesConditionType;
import emanondev.itemtag.activity.condition.LuckPermGroupConditionType;
import emanondev.itemtag.activity.condition.PermissionConditionType;
import emanondev.itemtag.activity.condition.WorldConditionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

public class ConditionManager {

    private static final HashMap<String, ConditionType> conditionTypes = new HashMap<>();

    public static @Nullable ConditionType.Condition read(@NotNull String line) {
        int index = line.indexOf(" ");
        String id = (index == -1 ? line : line.substring(0, index)).toLowerCase(Locale.ENGLISH);
        String info = index == -1 ? "" : line.substring(index + 1);
        boolean reversed = id.startsWith("!");
        ConditionType type = getConditionType(id);
        if (type==null)
            return null;
        return type.read(info,reversed);
    }

    public static void register(@NotNull ConditionType condition) {
        String id = condition.getId();
        if (conditionTypes.containsKey(id))
            throw new IllegalArgumentException();
        conditionTypes.put(id, condition);
        ItemTag.get().log("ConditionManager registered Condition Type &e"+condition.getId());
    }

    public static void load() {
        register(new PermissionConditionType());
        register(new WorldConditionType());
        register(new LuckPermGroupConditionType()); //doesn't actually require luckperms
        register(new HasUsesConditionType());
        //TODO event
    }

    /**
     * accepts reversed id (like both permission and !permission
     */
    public static @Nullable ConditionType getConditionType(@NotNull String id) {
        id = id.toLowerCase(Locale.ENGLISH);
        return conditionTypes.get(id.startsWith("!") ? id.substring(1) : id);
    }

    public static Collection<String> getConditionTypeIds() {
        return Collections.unmodifiableSet(conditionTypes.keySet());
    }
}

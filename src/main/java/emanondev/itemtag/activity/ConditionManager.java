package emanondev.itemtag.activity;

import emanondev.itemtag.activity.condition.HasUsesConditionType;
import emanondev.itemtag.activity.condition.LuckPermGroupConditionType;
import emanondev.itemtag.activity.condition.PermissionConditionType;
import emanondev.itemtag.activity.condition.WorldConditionType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;

public class ConditionManager {

    private static final HashMap<String, ConditionType> actionTypes = new HashMap<>();

    public static @NotNull ConditionType.Condition read(@NotNull String line) {
        int index = line.indexOf(" ");
        String id = (index == -1 ? line : line.substring(0, index)).toLowerCase(Locale.ENGLISH);
        String info = index == -1 ? "" : line.substring(index + 1);
        boolean reversed = info.startsWith("!");
        return actionTypes.get(id).read(reversed ? info.substring(1) : info, reversed);
    }

    public static void register(@NotNull ConditionType action) {
        String id = action.getId();
        if (actionTypes.containsKey(id))
            throw new IllegalArgumentException();
        actionTypes.put(id, action);
    }

    public static void load(){
        register(new PermissionConditionType());
        register(new WorldConditionType());
        register(new LuckPermGroupConditionType()); //doesn't actually require luckperms
        register(new HasUsesConditionType());
        //TODO event
    }
}

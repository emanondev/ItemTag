package emanondev.itemtag.activity;

import emanondev.itemtag.ItemTag;
import emanondev.itemtag.activity.condition.*;
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
        if (type == null)
            return null;
        return type.read(info, reversed);
    }

    public static void register(@NotNull ConditionType condition) {
        String id = condition.getId();
        if (conditionTypes.containsKey(id))
            throw new IllegalArgumentException();
        conditionTypes.put(id, condition);
        ItemTag.get().log("ConditionManager registered Condition Type &e" + condition.getId());
    }

    public static void load() {
        register(new PermissionConditionType());
        register(new WorldConditionType());
        register(new LuckPermGroupConditionType()); //doesn't actually require luckperms
        register(new HasUsesConditionType());
        register(new IsPvpConditionType());
        register(new AirLevelConditionType());
        register(new FoodLevelConditionType());
        register(new HealthConditionType());
        register(new IsSneakingConditionType());
        register(new IsSprintingConditionType());
        register(new XLocConditionType());
        register(new YLocConditionType());
        register(new ZLocConditionType());
        register(new TimeConditionType());
        register(new IsNightConditionType());
        register(new IsDayConditionType());
        register(new IsDawnConditionType());
        register(new IsDuskConditionType());
        register(new IsSunnyConditionType());
        register(new IsRainingConditionType());
        register(new IsThunderingConditionType());
        register(new EnvironmentConditionType());
        register(new IsOnGroundConditionType());
        register(new IsInWaterConditionType());
        register(new IsFlyingConditionType());
        register(new IsInvulnerableConditionType());
        register(new IsFrozenConditionType());
        register(new IsOnFireConditionType());
        register(new IsOutsideConditionType());
        register(new IsFullChargedConditionType()); //1.13+?
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

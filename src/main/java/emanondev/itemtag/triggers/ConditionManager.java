package emanondev.itemtag.triggers;

import emanondev.itemtag.activity.TriggerType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ConditionManager {

    private static final HashMap<String, ConditionType> map = new HashMap<>();

    public static void registerConditionType(@NotNull ConditionType type) {
        if (map.containsKey(type.getID()))
            throw new IllegalArgumentException();
        map.put(type.getID(), type);
    }

    public static List<ConditionType> getCompatibleConditionTypes(TriggerType type){
        List<ConditionType> list = new ArrayList<>();
        for (ConditionType conditionType:map.values())
            if (type.isCompatible(conditionType))
                list.add(conditionType);
        return list;
    }


    public static ConditionType getConditionType(@Nullable String id){
        return id==null?null:map.get(id);
    }

    public static Collection<ConditionType> getConditionTypes(){
        return Collections.unmodifiableCollection(map.values());
    }

    public static @NotNull Condition getCondition(@NotNull Map<String, Object> condMap) {
        try {
            ConditionType type = getConditionType((String) condMap.get("type"));
            return type == null ? new Condition(condMap) : type.readCondition(condMap);
        }catch (Exception e){
            return new Condition(condMap);
        }
    }
}

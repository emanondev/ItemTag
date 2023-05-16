package emanondev.itemtag.triggers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

public class Action {

    private final ActionType type;
    private List<Condition> conditions = null;
    private final Map<String, Object> rawValues = new LinkedHashMap<>();


    public Action(@NotNull Map<String, Object> values) {
        rawValues.putAll(values);
        type = ActionManager.getActionType((String) values.get("type"));
        if (type == null)
            throw new IllegalArgumentException();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>(rawValues);
        //map.put("type", type.getID());
        List<Map<String, Object>> conds = new ArrayList<>();
        for (Condition condition : conditions)
            conds.add(condition.toMap());
        map.put("conditions", conds);
        return map;
    }

    public int getConsumeUses() {
        return ((Number) rawValues.getOrDefault("consumes", 0)).intValue();
    }

    public void setConsumeUses(int value) {
        if (value < -1)
            value = -1;
        if (value == 0)
            rawValues.remove("consumes");
        else
            rawValues.put("consumes", value);
    }

    @UnmodifiableView
    public List<Condition> getConditions() {
        if (conditions == null) {
            try {
                conditions = new ArrayList<>();
                for (Map<String, Object> condMap : (List<Map<String, Object>>) rawValues.getOrDefault("conditions", new ArrayList<Map<String, Object>>())) {
                    try {
                        conditions.add(ConditionManager.getCondition(condMap));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        return Collections.unmodifiableList(conditions);
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions.clear();
        if (conditions != null)
            this.conditions.addAll(conditions);
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

    public void execute() {
        if (type == null)
            return;
        type.execute(this);
    }
}

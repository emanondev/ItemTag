package emanondev.itemtag.triggers;

import com.google.gson.Gson;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.List;

public class Trigger<E extends Event> {

    public TriggerType<E> getType() {
        return type;
    }

    public String getID(){
        return getType().getID();
    }

    private final TriggerType<E> type;
    private Map<String,Object> rawValues = new LinkedHashMap<>();
    private List<Condition> conditions = null;
    private List<Action> actions = null;

    public Trigger(@NotNull TriggerType<E> type, @NotNull String json){
        this.type = type;
        Map<String,Object> map = new Gson().fromJson(json,Map.class);
        if (map!=null)
            rawValues.putAll(map);
    }

    public String toJson(){
        return new Gson().toJson(rawValues);
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
    public List<Action> getActions() {
        if (actions == null) {
            try {
                actions = new ArrayList<>();
                for (Map<String, Object> condMap : (List<Map<String, Object>>) rawValues.getOrDefault("actions", new ArrayList<Map<String, Object>>())) {
                    try {
                        actions.add(ActionManager.getAction(condMap));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return Collections.unmodifiableList(actions);
    }

    public void setActions(List<Action> actions) {
        this.actions.clear();
        if (actions != null)
            this.actions.addAll(actions);
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

    public void handle(E event) {
        List<Condition> conds = getConditions();
        //boolean valid = true;
        for (Condition cond:conds){
            if (!cond.isSatisfied(event)){
                //TODO valid = false;
                return;
            }
        }

        for (Action action:getActions()){

        }
    }
}

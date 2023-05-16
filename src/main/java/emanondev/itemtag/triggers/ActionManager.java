package emanondev.itemtag.triggers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ActionManager {

    private static final HashMap<String, ActionType> map = new HashMap<>();

    public static void registerTrigger(@NotNull ActionType type) {
        if (map.containsKey(type.getID()))
            throw new IllegalArgumentException();
        map.put(type.getID(), type);
    }

    public static ActionType getActionType(@Nullable String id) {
        return id == null ? null : map.get(id);
    }

    public static Collection<ActionType> getActionTypes() {
        return Collections.unmodifiableCollection(map.values());
    }

    public static @NotNull Action getAction(@NotNull Map<String, Object> actionMap) {
        try {
            ActionType type = getActionType((String) actionMap.get("type"));
            return type == null ? new Action(actionMap) : type.readAction(actionMap);
        } catch (Exception e) {
            return new Action(actionMap);
        }
    }
}

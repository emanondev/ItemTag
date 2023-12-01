package emanondev.itemtag.activity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class TriggerManager {

    private static final HashMap<String, TriggerType> map = new HashMap<>();

    public static void registerTriggerType(@NotNull TriggerType type) {
        if (map.containsKey(type.getID()))
            throw new IllegalArgumentException();
        map.put(type.getID(), type);
    }

    public static TriggerType getTriggerType(@Nullable String id){
        return id==null?null:map.get(id);
    }

    public static Collection<TriggerType> getTriggerTypes(){
        return Collections.unmodifiableCollection(map.values());
    }
}

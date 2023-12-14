package emanondev.itemtag.activity;

import emanondev.itemtag.ItemTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class TriggerManager {

    private static final HashMap<String, TriggerType> triggerTypes = new HashMap<>();

    public static void registerTriggerType(@NotNull TriggerType type) {
        if (triggerTypes.containsKey(type.getId()))
            throw new IllegalArgumentException();
        triggerTypes.put(type.getId(), type);
        ItemTag.get().log("TriggerManager registered Trigger Type &e"+type.getId());
    }

    public static TriggerType getTriggerType(@Nullable String id){
        return id==null?null: triggerTypes.get(id);
    }

    public static Collection<TriggerType> getTriggerTypes(){
        return Collections.unmodifiableCollection(triggerTypes.values());
    }

    public static Collection<String> getTriggerTypeIds() {
        return Collections.unmodifiableSet(triggerTypes.keySet());
    }


    public static void load(){
        ItemTag.get().registerListener(new TriggerListener());
        TriggerManager.registerTriggerType(TriggerListener.CONSUME_ITEM);
        TriggerManager.registerTriggerType(TriggerListener.RIGHT_INTERACT);
        TriggerManager.registerTriggerType(TriggerListener.LEFT_INTERACT);
    }
}

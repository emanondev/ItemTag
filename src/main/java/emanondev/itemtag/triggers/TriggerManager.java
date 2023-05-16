package emanondev.itemtag.triggers;

import emanondev.itemtag.TagItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class TriggerManager {

    private static final HashMap<String, TriggerType> map = new HashMap<>();

    public static void registerTriggerType(@NotNull TriggerType type) {
        if (map.containsKey(type.getID()))
            throw new IllegalArgumentException();
        map.put(type.getID(), type);
    }

    public static boolean hasTrigger(TagItem tag, TriggerType type) {
        if (!tag.isValid())
            return false;
        List<String> l = tag.getStringList("triggers");
        return l != null && l.contains(type.getID());
    }

    public static Trigger getTrigger(TagItem tag, TriggerType type) {
        if (!hasTrigger(tag, type))
            return null;
        return type.generate(tag.getString("trigger_" + type.getID()));
    }

    public static TriggerType getTriggerType(@Nullable String id){
        return id==null?null:map.get(id);
    }

    public static Collection<TriggerType> getTriggerTypes(){
        return Collections.unmodifiableCollection(map.values());
    }
}

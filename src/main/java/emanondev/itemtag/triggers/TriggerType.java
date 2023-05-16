package emanondev.itemtag.triggers;

import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class TriggerType <E extends Event> {

    private final String id;
    private final Class<E> eventType;

    public TriggerType(String id, Class<E> e) {
        this.id = id.toLowerCase(Locale.ENGLISH);
        this.eventType = e;
    }

    public String getID() {
        return id;
    }

    public <N extends Event> boolean isCompatible(ConditionType<N> condition){
        return condition.getClass()==null||eventType.isAssignableFrom(condition.getClass());
    }

    public Trigger generate(@Nullable String json) {
        return new Trigger(this,json);
    }
}

package emanondev.itemtag.triggers;

import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Map;

public abstract class ConditionType <E extends Event>{


    private final String id;
    private final Class<E> eventType;

    public ConditionType(@NotNull String id, Class<E> e) {
        this.id = id.toLowerCase(Locale.ENGLISH);
        this.eventType = e;
    }

    public String getID() {
        return id;
    }

    public Class<E> getEventType(){
        return eventType;
    }

    public @NotNull Condition readCondition(@NotNull Map<String, Object> condMap) {
        return new Condition(condMap);
    }

    public abstract boolean isSatisfied(Condition condition, E e);
}

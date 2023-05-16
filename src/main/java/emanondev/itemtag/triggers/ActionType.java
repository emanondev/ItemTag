package emanondev.itemtag.triggers;

import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Map;

public abstract class ActionType<E extends Event> {

    private final String id;
    private final Class<E> eventType;

    public ActionType(String id, Class<E> e) {
        this.id = id.toLowerCase(Locale.ENGLISH);
        this.eventType = e;
    }

    public String getID() {
        return id;
    }

    public Class<E> getEventType() {
        return eventType;
    }

    public @NotNull Action readAction(Map<String, Object> condMap) {
        return new Action(condMap);
    }

    public abstract void execute(Action action);
}
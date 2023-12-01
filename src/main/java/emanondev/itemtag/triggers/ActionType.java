package emanondev.itemtag.triggers;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Map;

public abstract class ActionType<E extends Event> {

    private final String id;
    private final Class<E> eventType;

    public ActionType(@NotNull String id,@NotNull  Class<E> e) {
        this.id = id.toLowerCase(Locale.ENGLISH);
        this.eventType = e;
    }

    public @NotNull String getID() {
        return id;
    }

    public @NotNull Class<E> getEventType() {
        return eventType;
    }

    public @NotNull Action readAction(@NotNull Map<String, Object> condMap) {
        return new Action(condMap);
    }

    public abstract void execute(@NotNull Action action, @NotNull E event, @NotNull Player player);
}
package emanondev.itemtag.activity;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public abstract class ActionType {

    private final String id;
    private final Class<? extends Event> clazz;

    public ActionType(@NotNull String id) {
        this(id, null);
    }

    public ActionType(@NotNull String id, @Nullable Class<? extends Event> clazz) {
        if (!Pattern.compile("[a-z][_a-z0-9]*").matcher(id).matches())
            throw new IllegalArgumentException();
        this.id = id;
        this.clazz = clazz;
    }

    public abstract @NotNull Action read(@NotNull String info);

    public final @NotNull String getId() {
        return this.id;
    }

    public abstract class Action {

        private final String info;

        public Action(@NotNull String info) {
            this.info = info;
        }

        public final @NotNull String getInfo() {
            return info;
        }

        public final @NotNull String getId() {
            return ActionType.this.getId();
        }

        public @NotNull String toString() {
            return getId() + (getInfo().isEmpty() ? "" : (" " + getInfo()));
        }

        public abstract boolean execute(@NotNull Player player, @NotNull ItemStack item, @Nullable Event event);

        public boolean isAssignable(@Nullable Event event) {
            if (clazz == null)
                return true;
            return event != null && clazz.isAssignableFrom(event.getClass());
        }
    }
}

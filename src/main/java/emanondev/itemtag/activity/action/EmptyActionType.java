package emanondev.itemtag.activity.action;

import emanondev.itemtag.activity.ActionType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class EmptyActionType extends ActionType {

    public static final EmptyActionType INST = new EmptyActionType();

    private EmptyActionType() {
        super("empty");
    }

    /**
     * Differently by any other action type empty action type receive the full line (action type included)
     */
    @Override
    public @NotNull Action read(@NotNull String info) {
        return new EmptyAction(info);
    }

    public final class EmptyAction extends Action {

        public EmptyAction(@NotNull String info) {
            super(info);
        }

        public @NotNull String toString() {
            return getInfo();
        }

        @Override
        public boolean execute(@NotNull Player player, @NotNull ItemStack item, Event event) {
            return false;
        }

    }
}

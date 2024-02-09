package emanondev.itemtag.activity.condition;

import emanondev.itemtag.activity.ConditionType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EmptyConditionType extends ConditionType {

    public static final EmptyConditionType INST = new EmptyConditionType();

    private EmptyConditionType() {
        super("empty");
    }

    /**
     * Differently by any other condition type empty condition type receive the full line (action type included and reversed status)
     */
    @Override
    public @NotNull Condition read(@NotNull String info, boolean reversed) {
        return new EmptyCondition(info);
    }

    public final class EmptyCondition extends Condition {

        public EmptyCondition(@NotNull String info) {
            super(info, false);
        }

        public @NotNull String toString() {
            return getInfo();
        }

        @Override
        public boolean evaluate(@NotNull Player player, @NotNull ItemStack item, Event event) {
            return false;
        }

        @Override
        protected boolean evaluateImpl(@NotNull Player player, @NotNull ItemStack item, Event event) {
            return false;
        }
    }
}

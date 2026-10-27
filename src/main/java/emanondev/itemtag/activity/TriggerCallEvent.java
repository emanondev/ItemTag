package emanondev.itemtag.activity;

import emanondev.itemtag.actions.Action;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TriggerCallEvent implements Cancellable {

    private final TriggerType trigger;
    @Getter
    private final Player player;
    @Getter
    private final ItemStack itemOld;
    @Getter
    private final ArrayList<Action> actions;
    private boolean cancelled = false;
    @Setter
    @Getter
    private ItemStack itemNew;

    public TriggerCallEvent(TriggerType trigger, Player player, ItemStack itemOld, ItemStack itemNew, List<Action> actions) {
        this.trigger = trigger;
        this.player = player;
        this.itemOld = itemOld;
        this.itemNew = itemNew;
        this.actions = new ArrayList<>(actions);
    }

    public TriggerType getTriggerType() {
        return trigger;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}

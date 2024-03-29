package emanondev.itemtag.activity;

import emanondev.itemtag.actions.Action;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TriggerCallEvent implements Cancellable {

    private final TriggerType trigger;
    private final Player player;
    private final ItemStack itemOld;
    private final ArrayList<Action> actions;
    private boolean cancelled = false;
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

    public Player getPlayer() {
        return player;
    }

    public ItemStack getItemOld() {
        return itemOld;
    }

    public ItemStack getItemNew() {
        return itemNew;
    }

    public void setItemNew(ItemStack itemNew) {
        this.itemNew = itemNew;
    }

    public ArrayList<Action> getActions() {
        return actions;
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

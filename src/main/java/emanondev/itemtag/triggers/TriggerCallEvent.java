package emanondev.itemtag.triggers;

import emanondev.itemtag.actions.Action;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TriggerCallEvent implements Cancellable {

    private boolean cancelled = false;

    public Trigger getTrigger() {
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

    private final Trigger trigger;
    private final Player player;
    private final ItemStack itemOld;
    private ItemStack itemNew;
    private final ArrayList<Action> actions;

    public TriggerCallEvent(Trigger trigger, Player player, ItemStack itemOld, ItemStack itemNew, List<Action> actions){
        this.trigger = trigger;
        this.player = player;
        this.itemOld = itemOld;
        this.itemNew = itemNew;
        this.actions = new ArrayList<>(actions);
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

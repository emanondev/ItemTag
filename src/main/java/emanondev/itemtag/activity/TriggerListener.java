package emanondev.itemtag.activity;

import emanondev.itemtag.ItemTag;
import emanondev.itemtag.TagItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class TriggerListener implements Listener {

    public static final TriggerType<PlayerItemConsumeEvent> CONSUME_ITEM = new TriggerType<>("consume_item", PlayerItemConsumeEvent.class);
    public static final TriggerType<PlayerInteractEvent> RIGHT_INTERACT = new TriggerType<>("right_interact", PlayerInteractEvent.class);
    public static final TriggerType<PlayerInteractEvent> LEFT_INTERACT = new TriggerType<>("left_interact", PlayerInteractEvent.class);

    @EventHandler
    private void event(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        TagItem tagItem = ItemTag.getTagItem(item);
        if (!tagItem.isValid())
            return;
        try {
            CONSUME_ITEM.handle(event, event.getPlayer(), item, event.getHand());
        } catch (Exception e) {
            CONSUME_ITEM.handle(event, event.getPlayer(), item, EquipmentSlot.HAND);
        }
    }

    @EventHandler
    private void event(PlayerInteractEvent event) {
        switch (event.getAction()) {
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK: {
                try {
                    RIGHT_INTERACT.handle(event, event.getPlayer(), event.getItem(), event.getHand());
                } catch (Exception e) {
                    RIGHT_INTERACT.handle(event, event.getPlayer(), event.getItem(), EquipmentSlot.HAND);
                }
            }
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK: {
                try {
                    LEFT_INTERACT.handle(event, event.getPlayer(), event.getItem(), event.getHand());
                } catch (Exception e) {
                    LEFT_INTERACT.handle(event, event.getPlayer(), event.getItem(), EquipmentSlot.HAND);
                }
            }
        }
    }
}
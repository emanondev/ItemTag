package emanondev.itemtag.activity;

import emanondev.itemtag.ItemTag;
import emanondev.itemtag.TagItem;
import emanondev.itemtag.triggers.Trigger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class TriggerListener implements Listener {

    public static final TriggerType CONSUME_ITEM = new TriggerType("consume_item", PlayerItemConsumeEvent.class);
    public static final TriggerType RIGHT_INTERACT = new TriggerType("right_interact", PlayerInteractEvent.class);
    public static final TriggerType LEFT_CLICK = new TriggerType("left_interact", PlayerInteractEvent.class);

    @EventHandler
    private void event(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        TagItem tagItem = ItemTag.getTagItem(item);
        if (!tagItem.isValid())
            return;
        Trigger trigger = TriggerManager.getTrigger(tagItem, CONSUME_ITEM);
        if (trigger != null)
            try { //TODO
                trigger.handle(event, tagItem,item, event.getHand());
            } catch (Exception e) {
                trigger.handle(event, tagItem,item, EquipmentSlot.HAND);
            }
    }

    @EventHandler
    private void event(PlayerInteractEvent event) {
        switch (event.getAction()) {
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK: {
                if (event.getItem()==null|| !event.getItem().hasItemMeta())
                    return;
                ItemStack item = event.getItem();
                TagItem tagItem = ItemTag.getTagItem(item);
                //TriggerTy trigger = TriggerManager.getTrigger(tagItem, RIGHT_CLICK);
                try { //TODO
                        RIGHT_INTERACT.handle(event, tagItem,item, event.getHand());
                    } catch (Exception e) {
                        trigger.handle(event, tagItem,item, EquipmentSlot.HAND);
                    }
            }
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK: {
                ItemStack item = event.getItem();
                TagItem tagItem = ItemTag.getTagItem(item);
                Trigger<PlayerInteractEvent> trigger = TriggerManager.getTrigger(tagItem, LEFT_CLICK);
                if (trigger != null)
                    try { //TODO
                        trigger.handle(event, tagItem,item, event.getHand());
                    } catch (Exception e) {
                        trigger.handle(event, tagItem,item, EquipmentSlot.HAND);
                    }
            }
        }
    }
}
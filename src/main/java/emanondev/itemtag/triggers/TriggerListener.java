package emanondev.itemtag.triggers;

import emanondev.itemedit.UtilsInventory;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.TagItem;
import emanondev.itemtag.actions.ActionHandler;
import emanondev.itemtag.command.itemtag.Actions;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class TriggerListener implements Listener {

    public static final TriggerType CONSUME_ITEM = new TriggerType<>("consume_item", PlayerItemConsumeEvent.class);

    @EventHandler
    private void event(PlayerItemConsumeEvent event){
        ItemStack item = event.getItem();
        TagItem tagItem = ItemTag.getTagItem(item);
        if (!tagItem.isValid())
            return;
        Trigger<PlayerItemConsumeEvent> trigger = TriggerManager.getTrigger(tagItem,CONSUME_ITEM);
        if (trigger==null)
            return;
        trigger.handle(event);
    }

    @EventHandler
    private void event(PlayerInteractEvent event) {
        /*
        switch (event.getAction()) {
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                ItemStack item = event.getItem();
                TagItem tagItem = ItemTag.getTagItem(item);
                if (!hasActions(tagItem))
                    return;
                String permission = getPermission(tagItem);
                if (permission != null && !event.getPlayer().hasPermission(permission))
                    return;
                long cooldown = getCooldownMs(tagItem);
                if (cooldown > 0) {
                    String cooldownId = getCooldownId(tagItem);
                    if (ItemTag.get().getCooldownAPI().hasCooldown(event.getPlayer(), cooldownId))
                        return;
                    ItemTag.get().getCooldownAPI().setCooldown(event.getPlayer(), cooldownId, cooldown);
                    if (Actions.getVisualCooldown(tagItem))
                        event.getPlayer().setCooldown(item.getType(), (int) (cooldown / 50));
                }

                int uses = getUses(tagItem);
                if (uses == 0)
                    return;

                for (String action : Actions.getActions(tagItem))
                    try {
                        if (action.isEmpty())
                            continue;
                        ActionHandler.handleAction(event.getPlayer(), action.split(TYPE_SEPARATOR)[0],
                                action.split(TYPE_SEPARATOR)[1]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                if (uses > 0) {
                    if (uses == 1 && getConsume(tagItem)) {
                        if (event.getItem().getAmount() == 1) { //1.8 doesn't like  event.getItem().setAmount(event.getItem().getAmount() - 1); on single items
                            try {
                                if (event.getHand() == EquipmentSlot.HAND)
                                    event.getPlayer().getInventory().setItemInMainHand(null);
                                else
                                    event.getPlayer().getInventory().setItemInOffHand(null);
                            } catch (Error e) {
                                event.getPlayer().getInventory().setItemInHand(null);
                            }
                        } else
                            event.getItem().setAmount(event.getItem().getAmount() - 1);

                    } else {
                        try {
                            if (event.getItem().getAmount() == 1) {
                                setUses(tagItem, uses - 1);
                            } else {
                                ItemStack clone = event.getItem();
                                clone.setAmount(clone.getAmount() - 1);
                                if (event.getHand() == EquipmentSlot.HAND)
                                    event.getPlayer().getInventory().setItemInMainHand(clone);
                                else
                                    event.getPlayer().getInventory().setItemInOffHand(clone);
                                setUses(ItemTag.getTagItem(clone), uses - 1);
                                UtilsInventory.giveAmount(event.getPlayer(), clone, 1, UtilsInventory.ExcessManage.DROP_EXCESS);
                            }

                        } catch (Throwable t) { //1.8 compability
                            if (event.getItem().getAmount() == 1)
                                tagItem.setTag(TRIGGER_USES_KEY, uses - 1);
                            else {
                                ItemStack clone = event.getItem();
                                clone.setAmount(clone.getAmount() - 1);
                                event.getPlayer().getInventory().setItemInHand(clone);
                                ItemTag.getTagItem(clone).setTag(TRIGGER_USES_KEY, uses - 1);
                                UtilsInventory.giveAmount(event.getPlayer(), clone, 1, UtilsInventory.ExcessManage.DROP_EXCESS);
                            }
                        }
                    }

                }
            default:
        }*/
    }
}

package emanondev.itemtag.triggers;

import org.bukkit.event.player.PlayerItemConsumeEvent;

public class TriggerLoader {

    public static void load(){
        TriggerManager.registerTriggerType(TriggerListener.CONSUME_ITEM);
    }
}

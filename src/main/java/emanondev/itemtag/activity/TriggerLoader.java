package emanondev.itemtag.activity;

public class TriggerLoader {

    public static void load(){
        TriggerManager.registerTriggerType(TriggerListener.CONSUME_ITEM);
        TriggerManager.registerTriggerType(TriggerListener.RIGHT_CLICK);
        TriggerManager.registerTriggerType(TriggerListener.LEFT_CLICK);
    }
}

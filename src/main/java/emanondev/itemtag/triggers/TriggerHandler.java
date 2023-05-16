package emanondev.itemtag.triggers;

import emanondev.itemtag.ItemTag;
import emanondev.itemtag.TagItem;
import emanondev.itemtag.triggers.Trigger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TriggerHandler {

    /**
     * it triggers type <triggertype> actions add <#> <actiontype> [actionparams]
     * it triggers type <triggertype> actions swap <#> <#>
     * it triggers type <triggertype> actions delete <#>
     * it triggers type <triggertype> actions addcondition <#> <conditiontype> <conditionparams>
     * it triggers type <triggertype> actions deletecondition <#> <#>
     * it triggers type <triggertype> actions useconsume <#> <#>
     * it triggers type <triggertype> useconsume <#>
     * it triggers type <triggertype> conditions add <#> <conditiontype> <conditionparams>
     * it triggers type <triggertype> conditions delete <#>
     * it triggers type <triggertype> visualcooldown
     * it triggers type <triggertype> cooldown <#> [unit s/ms]
     * it triggers type <triggertype> cooldownid <id>
     * it triggers type <triggertype> permission <permission>
     * it triggers uses <#>
     * it triggers consume <true/false>
     */

    /*
    public Trigger<PlayerInteractEvent> RIGHT_CLICK = new Trigger<>("right_click");
    public Trigger<PlayerInteractEvent> LEFT_CLICK = new Trigger<>("left_click");
    public Trigger<PlayerToggleSneakEvent> SNEAK = new Trigger<>("sneak");
    public Trigger<PlayerItemConsumeEvent> EAT = new Trigger("eat");
    public Trigger RIGHT_CLICK_ENTITY = new Trigger("right_click_entity");
    public Trigger ATTACK_ENTITY = new Trigger("attack_entity");
    public Trigger ATTACK_PLAYER = new Trigger("attack_player");*/


    private final static String TRIGGERS_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":triggertype_";
    private final static String TRIGGER_USES_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":triggeruses";
    private final static String TRIGGER_CONSUME_AT_END_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":triggerconsume";
    private final static String TRIGGER_VISUAL_COOLDOWN = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":triggervisualcooldown";
    private final static String TRIGGER_COOLDOWN_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":triggercooldown";
    private final static String TRIGGER_COOLDOWN_ID_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":triggercooldown_id";
    private final static String TRIGGER_PERMISSION_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":triggerpermission";



    public boolean hasTrigger(Trigger trigger,TagItem item){
        return  item.hasStringTag(getTriggerKey(trigger));
    }

    public String getTriggerKey(Trigger trigger){
        return TRIGGERS_KEY+trigger.getType().getID();
    }

    /*
    public static void handleTriggerCall(Player player, TagItem item, String trigger) {
        if ()
        Trigger trig = triggers.get(type);
        ItemStack item = player.getInventory().getItemInHand();
        if ()
    }*/

    private static final HashMap<String, Trigger<Event>> triggers = new HashMap<>();

    public static void registerTrigger(Trigger trigger) {
        if (trigger == null)
            throw new NullPointerException();
        if (triggers.containsKey(trigger.getType().getID()))
            throw new IllegalArgumentException();
        triggers.put(trigger.getType().getID().toLowerCase(Locale.ENGLISH), trigger);
    }

    public static void clearTriggers() {
        triggers.clear();
    }

    public static List<Trigger> getTriggers() {
        return new ArrayList<>(triggers.values());
    }

    public static List<String> getTypes() {
        return new ArrayList<>(triggers.keySet());
    }

    public static Trigger getTrigger(String type) {
        return triggers.get(type.toLowerCase(Locale.ENGLISH));
    }

}

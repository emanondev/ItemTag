package emanondev.itemtag.activity;

import emanondev.itemtag.ItemTag;
import emanondev.itemtag.TagItem;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TriggerHandler {
    /*
     * it triggers type <triggertype> actions add <#> <actiontype> [actionparams]
     * it triggers type <triggertype> actions swap <#> <#>
     * it triggers type <triggertype> actions delete <#>
     * it triggers type <triggertype> actions addcondition <#> <conditiontype> <conditionparams>
     * it triggers type <triggertype> actions deletecondition <#> <#>
     * it triggers type <triggertype> conditions add <#> <conditiontype> <conditionparams>
     * it triggers type <triggertype> conditions delete <#>
     */
    /**
     * it triggers activity <triggertype> [activity]
     * it triggers useconsume   <#>
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


    private final static String TRIGGERS_ACTIVITY_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":t_activity_";
    private final static String TRIGGERS_SLOTS_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":t_slots_";
    //private final static String TRIGGER_USES_CONSUME_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":t_useconsume_";
    private final static String TRIGGER_COOLDOWN_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":t_cooldown_";
    //private final static String TRIGGER_COOLDOWN_ID_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":t_cooldown_id_";

    private final static String TRIGGER_USES_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":t_uses";
    private final static String TRIGGER_MAXUSES_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":t_maxuses";
    private final static String TRIGGER_NOT_CONSUME_AT_END_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":t_notconsume";
    private final static String TRIGGER_DISPLAY_USES = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":t_displayuses";
    private final static String TRIGGER_VISUAL_COOLDOWN = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":t_visualcooldown";
    private final static String TRIGGER_PERMISSION_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":t_perm";


    public static boolean hasTrigger(@NotNull TriggerType trigger, @NotNull TagItem item) {
        return getTriggerActivity(trigger, item) != null;
    }

    /**
     * @return Activity id if present
     */
    public static String getTriggerActivityId(@NotNull TriggerType trigger, @NotNull TagItem item) {
        return item.getString(TRIGGERS_ACTIVITY_KEY + trigger.getId());
    }

    /**
     * @return Activity if present
     */
    public static @Nullable Activity getTriggerActivity(@NotNull TriggerType trigger, @NotNull TagItem item) {
        String id = getTriggerActivityId(trigger, item);
        return id == null ? null : ActivityManager.getActivity(id);
    }

    public static void setTriggerActivity(@NotNull TriggerType trigger, @NotNull TagItem item, @Nullable Activity activity) {
        if (activity == null)
            item.removeTag(TRIGGERS_ACTIVITY_KEY + trigger.getId());
        else
            item.setTag(TRIGGERS_ACTIVITY_KEY + trigger.getId(), activity.getId());
    }

    public static @NotNull Collection<EquipmentSlot> getAllowedSlots(@NotNull TriggerType trigger, @NotNull TagItem item) {
        String values = item.getString(TRIGGERS_SLOTS_KEY + trigger.getId());
        if (values == null || values.isEmpty())
            return EnumSet.allOf(EquipmentSlot.class);
        EnumSet<EquipmentSlot> slots = EnumSet.noneOf(EquipmentSlot.class);
        for (String slot : values.split(","))
            slots.add(EquipmentSlot.valueOf(slot));
        return slots;
    }

    /**
     * setting none is like setting all
     */
    public static void setAllowedSlot(@NotNull TriggerType trigger, @NotNull TagItem item, @NotNull Collection<EquipmentSlot> slots) {
        StringBuilder value = null;
        boolean started = false;
        if (!slots.isEmpty() && slots.size() != EquipmentSlot.values().length) {
            value = new StringBuilder();
            for (EquipmentSlot slot : slots) {
                value.append(started ? "," : "").append(slot.name());
                started = true;
            }
        }
        if (value == null)
            item.removeTag(TRIGGERS_SLOTS_KEY + trigger.getId());
        else
            item.setTag(TRIGGERS_SLOTS_KEY + trigger.getId(), value.toString());
    }

    public static boolean isConsumeAtUsesEnd(@NotNull TagItem item) {
        return !item.hasBooleanTag(TRIGGER_NOT_CONSUME_AT_END_KEY);
    }

    public static void setConsumeAtUsesEnd(@NotNull TagItem item, boolean value) {
        if (value)
            item.removeTag(TRIGGER_NOT_CONSUME_AT_END_KEY);
        else
            item.setTag(TRIGGER_NOT_CONSUME_AT_END_KEY, true);
    }


    public static boolean isDisplayUses(@NotNull TagItem item) {
        return item.hasBooleanTag(TRIGGER_DISPLAY_USES);
    }

    public static void setDisplayUses(@NotNull TagItem item, boolean value) {
        if (!value)
            item.removeTag(TRIGGER_DISPLAY_USES);
        else
            item.setTag(TRIGGER_DISPLAY_USES, false);
    }
/*
    public static int getConsumeUses(@NotNull TriggerType trigger, @NotNull TagItem item) {
        Integer value = item.getInteger(TRIGGER_USES_CONSUME_KEY + trigger.getId());
        return value == null ? 1 : value;
    }

    public static void setConsumeUses(@NotNull TriggerType trigger, @NotNull TagItem item, int value) {
        if (value == 1)
            item.removeTag(TRIGGER_USES_CONSUME_KEY + trigger.getId());
        else
            item.setTag(TRIGGER_USES_CONSUME_KEY + trigger.getId(), value);
    }*/

    public static int getUsesLeft( @NotNull TagItem item) {
        Integer value = item.getInteger(TRIGGER_USES_KEY);
        return value == null ? 1 : value;
    }

    public static void setUsesLeft( @NotNull TagItem item, int value) {
        if (value == 1)
            item.removeTag(TRIGGER_USES_KEY );
        else
            item.setTag(TRIGGER_USES_KEY , Math.max(-1, value));
    }


    public static int getMaxUses( @NotNull TagItem item) {
        Integer value = item.getInteger(TRIGGER_MAXUSES_KEY);
        return value == null ? 1 : value;
    }

    public static void setMaxUses( @NotNull TagItem item, int value) {
        if (value == 1)
            item.removeTag(TRIGGER_MAXUSES_KEY );
        else
            item.setTag(TRIGGER_MAXUSES_KEY , Math.max(-1, value));
    }

    public static boolean hasVisualCooldown(@NotNull TagItem item) {
        return item.hasBooleanTag(TRIGGER_VISUAL_COOLDOWN);
    }

    public static void setVisualCooldown(@NotNull TagItem item, boolean value) {
        if (!value)
            item.removeTag(TRIGGER_VISUAL_COOLDOWN);
        else
            item.setTag(TRIGGER_VISUAL_COOLDOWN, true);
    }


    public static long getCooldownAmountMs(@NotNull TriggerType trigger, @NotNull TagItem item) {
        Integer value = item.getInteger(TRIGGER_COOLDOWN_KEY + trigger.getId());
        return value == null ? 0L : value * 10;
    }

    public static void setCooldownAmountMs(@NotNull TriggerType trigger, @NotNull TagItem item, long value) {
        if (value == 0)
            item.removeTag(TRIGGER_COOLDOWN_KEY + trigger.getId());
        else
            item.setTag(TRIGGER_COOLDOWN_KEY + trigger.getId(), (int) value / 10);
    }

    public static String getCooldownId(@NotNull TriggerType trigger /*, @NotNull TagItem item*/) {
        return "t_cd_"+trigger.getId();
        //String value = item.getString(TRIGGER_COOLDOWN_ID_KEY + trigger.getId());
        //return value == null ? "default" : value;
    }

    /*
    public static void setCooldownId(@NotNull TriggerType trigger, @NotNull TagItem item, String value) {
        if (value == null || value.isEmpty())
            item.removeTag(TRIGGER_COOLDOWN_ID_KEY + trigger.getId());
        else
            item.setTag(TRIGGER_COOLDOWN_ID_KEY + trigger.getId(), value);
    }*/

    public static String getPermission(@NotNull TagItem item) {
        return item.getString(TRIGGER_PERMISSION_KEY);
    }

    public static void setPermission(@NotNull TagItem item, String value) {
        if (value == null || value.isEmpty())
            item.removeTag(TRIGGER_PERMISSION_KEY);
        else
            item.setTag(TRIGGER_PERMISSION_KEY, value);
    }





    /*
    public static void handleTriggerCall(Player player, TagItem item, String trigger) {
        if ()
        Trigger trig = triggers.get(type);
        ItemStack item = player.getInventory().getItemInHand();
        if ()
    }*/

    private static final HashMap<String, TriggerType> triggers = new HashMap<>();

    public static void registerTrigger(TriggerType trigger) {
        if (trigger == null)
            throw new NullPointerException();
        if (triggers.containsKey(trigger.getId()))
            throw new IllegalArgumentException();
        triggers.put(trigger.getId().toLowerCase(Locale.ENGLISH), trigger);
    }

    public static void clearTriggers() {
        triggers.clear();
    }

    public static List<TriggerType> getTriggers() {
        return new ArrayList<>(triggers.values());
    }

    public static List<String> getTypes() {
        return new ArrayList<>(triggers.keySet());
    }

    public static TriggerType getTrigger(String type) {
        return triggers.get(type.toLowerCase(Locale.ENGLISH));
    }

}

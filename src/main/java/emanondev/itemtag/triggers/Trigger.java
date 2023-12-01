package emanondev.itemtag.triggers;

import com.google.gson.Gson;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.TagItem;
import emanondev.itemtag.activity.TriggerType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

public class Trigger {

    public TriggerType getType() {
        return type;
    }

    public String getID() {
        return getType().getID();
    }

    private final TriggerType type;
    private final Map<String, Object> rawValues = new LinkedHashMap<>();
    private List<Condition> conditions = null;
    private List<Action> actions = null;
    private long cooldown = 0;
    private String cooldownId = null;
    private String permission = null;

    public Trigger(@NotNull TriggerType type, @NotNull String json) {
        this.type = type;
        Map<String, Object> map = new Gson().fromJson(json, Map.class);
        if (map != null) {
            rawValues.putAll(map);
            cooldown = ((Number) rawValues.getOrDefault("cd", 0L)).longValue();
            cooldownId = (String) rawValues.getOrDefault("cdId", null);
            permission = (String) rawValues.getOrDefault("perm", null);
        }
    }

    public @Nullable String getPermission() {
        return permission;
    }

    public void setPermission(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            rawValues.remove("perm");
            permission = null;
        } else {
            rawValues.put("perm", value);
            permission = value;
        }
    }

    public @NotNull String getCooldownId() {
        return cooldownId == null ? "default" : cooldownId;
    }

    public void setCooldownId(@Nullable String value) {
        if (value == null || value.equals("default")) {
            rawValues.remove("cdId");
            cooldownId = null;
        } else {
            rawValues.put("cdId", value);
            cooldownId = value;
        }
    }

    public long getCooldown() {
        return cooldown;
    }

    public void setCooldown(long value) {
        if (value <= 0) {
            rawValues.remove("cd");
            cooldown = 0L;
        } else {
            rawValues.put("cd", value);
            cooldown = value;
        }
    }

    public String toJson() {
        return new Gson().toJson(rawValues);
    }

    public int getConsumeUses() {
        return ((Number) rawValues.getOrDefault("consumes", 0)).intValue();
    }

    public void setConsumeUses(int value) {
        if (value < -1)
            value = -1;
        if (value == 0)
            rawValues.remove("consumes");
        else
            rawValues.put("consumes", value);
    }

    @UnmodifiableView
    public List<Action> getActions() {
        if (actions == null) {
            try {
                actions = new ArrayList<>();
                for (Map<String, Object> condMap : (List<Map<String, Object>>) rawValues.getOrDefault("actions", new ArrayList<Map<String, Object>>())) {
                    try {
                        actions.add(ActionManager.getAction(condMap));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return Collections.unmodifiableList(actions);
    }

    public void setActions(List<Action> actions) {
        this.actions.clear();
        if (actions != null)
            this.actions.addAll(actions);
    }

    @UnmodifiableView
    public List<Condition> getConditions() {
        if (conditions == null) {
            try {
                conditions = new ArrayList<>();
                for (Map<String, Object> condMap : (List<Map<String, Object>>) rawValues.getOrDefault("conditions", new ArrayList<Map<String, Object>>())) {
                    try {
                        conditions.add(ConditionManager.getCondition(condMap));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return Collections.unmodifiableList(conditions);
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions.clear();
        if (conditions != null)
            this.conditions.addAll(conditions);
    }

    public void setValue(@NotNull String key, @Nullable Object value) {
        if (value == null)
            rawValues.remove(key);
        else
            rawValues.put(key, value);
    }

    public <T, L extends T> @Nullable T getValue(@NotNull String key, @Nullable L def, @NotNull Class<T> clazz) {
        try {
            return (T) rawValues.getOrDefault(key, def);
        } catch (ClassCastException e) {
            e.printStackTrace();
            return def;
        }
    }

    /*
    public Player getTargetPlayer(E event) {
        try {
            if (event instanceof PlayerEvent)
                return ((PlayerEvent) event).getPlayer();
            if (event instanceof InventoryInteractEvent)
                return (Player) ((InventoryInteractEvent) event).getWhoClicked();
            if (event instanceof EntityEvent)
                return (Player) ((EntityEvent) event).getEntity();
            if (event instanceof BlockBreakEvent)
                return ((BlockBreakEvent) event).getPlayer();
            if (event instanceof BlockPlaceEvent)
                return ((BlockPlaceEvent) event).getPlayer();
            if (event instanceof BlockDispenseArmorEvent)
                return (Player) ((BlockDispenseArmorEvent) event).getTargetEntity();
            new IllegalStateException().printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }*/

    public boolean handle(@NotNull E event,@NotNull TagItem tagItem,@NotNull ItemStack item,@Nullable EquipmentSlot slot) {
        Player p = getTargetPlayer(event);
        if (p == null)
            return false;
        if (permission == null || !p.hasPermission(permission)) {
            //TODO no perm
            return false;
        }
        if (getCooldown() > 0)
            if (ItemTag.get().getCooldownAPI().hasCooldown(p, getCooldownId())) {
                //TODO on cooldown
                return false;
            } else {
                ItemTag.get().getCooldownAPI().setCooldown(p, getCooldownId(), getCooldown());
                if (hasVisualCooldown())
                    p.setCooldown(item.getType(), (int) (getCooldown() / 50));
            }

        List<Condition> conds = getConditions();
        //boolean valid = true;
        for (Condition cond : conds) {
            if (!cond.isSatisfied(event, p)) {
                //TODO valid = false;
                return false;
            }
        }

        int consumes = getConsumeUses();
        List<Action> toDoAction = new ArrayList<>();
        for (Action action : getActions()) {
            if (action.isSatisfied(event, p)) {
                consumes += action.getConsumeUses();
                toDoAction.add(action);
            }
        }
        if (toDoAction.isEmpty())
            return false;

        int uses = getUsesLeft(tagItem);


        /*
        //set item
        if (uses>=0 && consumes>0){
            if (uses==0)
                return false;
            uses = Math.max(0,uses-consumes);

            if (uses == 1 && getConsume(tagItem)) {
                if (item.getAmount() == 1) { //1.8 doesn't like  event.getItem().setAmount(event.getItem().getAmount() - 1); on single items
                    try {
                        if (event.getHand() == EquipmentSlot.HAND)
                            p.getInventory().setItemInMainHand(null);
                        else
                            p.getInventory().setItemInOffHand(null);
                    } catch (Error e) {
                        p.getInventory().setItemInHand(null);
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
                            p.getInventory().setItemInMainHand(clone);
                        else
                            p.getInventory().setItemInOffHand(clone);
                        setUses(ItemTag.getTagItem(clone), uses - 1);
                        UtilsInventory.giveAmount(p, clone, 1, UtilsInventory.ExcessManage.DROP_EXCESS);
                    }

                } catch (Throwable t) { //1.8 compability
                    if (event.getItem().getAmount() == 1)
                        tagItem.setTag(ACTION_USES_KEY, uses - 1);
                    else {
                        ItemStack clone = event.getItem();
                        clone.setAmount(clone.getAmount() - 1);
                        p.getInventory().setItemInHand(clone);
                        ItemTag.getTagItem(clone).setTag(ACTION_USES_KEY, uses - 1);
                        UtilsInventory.giveAmount(p, clone, 1, UtilsInventory.ExcessManage.DROP_EXCESS);
                    }
                }
            }

        }

        for (Action action : toDoAction)
            try {
                action.execute(event, p);
            } catch (Throwable t) {
                t.printStackTrace();
            }

*/
        return true;
    }

    private boolean hasVisualCooldown() {
        return false; //TODO
    }


    private int getUsesLeft(TagItem item){
        return 1; //TODO
    }
}

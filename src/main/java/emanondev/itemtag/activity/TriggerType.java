package emanondev.itemtag.activity;

import emanondev.itemedit.UtilsInventory;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.TagItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;

public class TriggerType<E extends Event> {

    private final String id;
    private final Class<E> eventType;

    public TriggerType(@NotNull String id, Class<E> e) {
        if (!Pattern.compile("[a-z][_a-z0-9]*").matcher(id).matches())
            throw new IllegalArgumentException();
        this.id = id;
        this.eventType = e;
    }

    public String getId() {
        return id;
    }

    public void handle(E event, @NotNull Player p, ItemStack item, EquipmentSlot slot) {
        TagItem tag = ItemTag.getTagItem(item);
        if (!tag.isValid())
            return;
        Activity activity = TriggerHandler.getTriggerActivity(this, tag);
        if (activity == null)
            return;
        if (!TriggerHandler.getAllowedSlots(this, tag).contains(slot))
            return;
        long ms = TriggerHandler.getCooldownAmountMs(this, tag);
        if (ms > 0 && ItemTag.get().getCooldownAPI().hasCooldown(p, TriggerHandler.getCooldownId(this, tag))) {

            return; //TODO on cooldown
        }

        //checking conditions
        boolean satisfied = true;
        for (ConditionType.Condition cond : activity.getConditions()) {
            if (!cond.isCompatible(event)) {
                ItemTag.get().log("Incompatible Condition &e" + cond + "&f from Activity &e"
                        + activity.getId() + "&f used on Trigger &e" + getId());
                return;
            }
            try {
                if (!cond.evaluate(p, item, event)) {
                    satisfied = false;
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                satisfied = false;
                break;
            }
        }
        //conditions checked, result on satisfied

        if (!satisfied) {
            executeActions(activity.getAlternativeActions(), event, item, p, activity,
                    "(alternative actions)");
            return;
        }

        int uses = TriggerHandler.getUsesLeft(tag);
        int newUses = uses;
        if (uses >= 0) {
            int consumes = activity.getConsumes();//TriggerHandler.getConsumeUses(this, tag);
            if (consumes > uses) {
                executeActions(activity.getNoConsumesActions(), event, item, p, activity,
                        "(no consume actions)");
                return;
            }
            int maxUses = TriggerHandler.getMaxUses(tag);
            newUses = Math.max(0, maxUses > 0 ? Math.min(maxUses, uses - consumes) : (uses - consumes));
        }

        executeActions(activity.getActions(), event, item, p, activity,
                "(actions)");

        //apply uses change
        if (uses < 0 || newUses == uses)//skip
            return;

        if (item.getAmount() == 1) {
            if (newUses == 0 && TriggerHandler.isConsumeAtUsesEnd(tag)) {
                //TODO delete

                return;
            }
            TriggerHandler.setUsesLeft(tag, newUses);
            //TODO set changes?
            return;
        }

        //stack size > 1

        if (newUses == 0 && TriggerHandler.isConsumeAtUsesEnd(tag)) {
            //TODO dec by 1
            item.setAmount(item.getAmount()-1);
            return;
        }

        ItemStack toGive = new ItemStack(item);
        //toGive.setAmount(1);
        item.setAmount(item.getAmount()-1);
        TagItem toGiveTag = ItemTag.getTagItem(toGive);
        TriggerHandler.setUsesLeft(toGiveTag,newUses);
        updateUsesDisplay(item);
        UtilsInventory.giveAmount(p,toGive,1, UtilsInventory.ExcessManage.DROP_EXCESS);
    }

    private void executeActions(Collection<ActionType.Action> actions, E event, ItemStack item, Player p, Activity activity,
                                String actionType) {
        for (ActionType.Action action : actions) {
            if (!action.isAssignable(event)) {
                ItemTag.get().log("Incompatible Action &e" + action + "&f from Activity " + actionType + " &e"
                        + activity.getId() + "&f used on Trigger &e" + getId() + "&f, skipping it");
                continue;
            }
            try {
                action.execute(p, item, event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateUsesDisplay(ItemStack item) {
        TagItem tagItem = ItemTag.getTagItem(item);
        boolean show = TriggerHandler.isDisplayUses(tagItem);
        Map<String, Object> metaMap = new LinkedHashMap<>(item.getItemMeta().serialize());
        /*if (show && !map.containsKey("meta"))
            map.put("meta",new LinkedHashMap<String,Object>());

        if (map.containsKey("meta")) {
            Map<String, Object> metaMap = (Map<String, Object>) map.get("meta");*/
        if (show && !metaMap.containsKey("lore"))
            metaMap.put("lore", new ArrayList<String>());

        if (metaMap.containsKey("lore")) {
            List<String> lore = new ArrayList<>((Collection<String>) metaMap.get("lore"));
            //that's a bit hardcoded!
            lore.removeIf((line) -> (line.startsWith(
                    "{\"italic\":false,\"color\":\"white\",\"translate\":\"item.durability\",\"with\":[{\"text\":\"")
                    && line.endsWith("\"}]}") || line.startsWith(
                    "{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"white\",\"text\":\"Durability:")
                    && line.endsWith("\"}],\"text\":\"\"}")));
            if (show) {
                int uses = TriggerHandler.getUsesLeft(tagItem);
                int maxUses = TriggerHandler.getMaxUses(tagItem);
                lore.add("{\"italic\":false,\"color\":\"white\",\"translate\":\"item.durability\",\"with\":[{\"text\":\"" +
                        (uses == -1 ? "∞" : uses) + "\"},{\"text\":\"" + (maxUses == -1 ? "∞" : maxUses) + "\"}]}");
            }
            if (!lore.isEmpty())
                metaMap.put("lore", lore);
            else
                metaMap.remove("lore");
        }
        metaMap.put("==", "ItemMeta");
        item.setItemMeta((ItemMeta) ConfigurationSerialization.deserializeObject(metaMap));
    }

    public ItemStack getGuiItem(@Nullable Player player){
        ItemStack mat;
        try {
            mat = new ItemStack(Material.valueOf(ItemTag.get().getConfig("triggers.yml")
                    .getString(getId() + ".gui_material", Material.STONE.name()).toUpperCase(Locale.ENGLISH)));
        } catch (Exception e){
            e.printStackTrace();
            mat = new ItemStack(Material.STONE);
        }
        ItemMeta meta = mat.getItemMeta();
        meta.addItemFlags(ItemFlag.values());
        meta.setDisplayName(ChatColor.AQUA+getId());
        meta.setLore(ItemTag.get().getLanguageConfig(player).loadMultiMessage("trigger."+getId()+".description",new ArrayList<>()));
        mat.setItemMeta(meta);
        return mat;
    }
}

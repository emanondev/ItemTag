package emanondev.itemtag.actions;

import emanondev.itemedit.utility.ItemUtils;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.TagItem;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class ActionsUtility {

    private static final String DEFAULT_COOLDOWN_ID = "default";

    public final static String TYPE_SEPARATOR = "%%:%%";
    public final static String ACTIONS_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":actions";
    public final static String ACTION_USES_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":uses";
    public final static String ACTION_MAXUSES_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":maxuses";
    public final static String ACTION_DISPLAYUSES_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":displayuses";
    public final static String ACTION_CONSUME_AT_END_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":consume";
    public final static String ACTION_VISUAL_COOLDOWN = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":visualcooldown";
    public final static String ACTION_COOLDOWN_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":cooldown";
    public final static String ACTION_COOLDOWN_ID_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":cooldown_id";
    public final static String ACTION_PERMISSION_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":permission";

    public static boolean hasActions(TagItem item) {
        return item.hasStringListTag(ACTIONS_KEY);
    }

    @Nullable
    public static List<String> getActions(TagItem item) {
        return item.getStringList(ACTIONS_KEY);
    }

    public static void setActions(TagItem item, List<String> actions) {
        if (actions == null || actions.isEmpty()) { // default
            item.removeTag(ACTIONS_KEY);
        } else {
            item.setTag(ACTIONS_KEY, actions);
        }
    }

    public static int getUses(TagItem item) {
        return item.getInteger(ACTION_USES_KEY, 1);
    }

    public static void setUses(TagItem item, int amount) {
        if (amount == 1) { // default
            item.removeTag(ACTION_USES_KEY);
        } else {
            item.setTag(ACTION_USES_KEY, Math.max(-1, amount));
        }
    }

    public static int getMaxUses(TagItem item) {
        return item.getInteger(ACTION_MAXUSES_KEY, -1);
    }

    public static void setMaxUses(TagItem item, int amount) {
        if (amount <= -1) { // default
            item.removeTag(ACTION_MAXUSES_KEY);
        } else {
            item.setTag(ACTION_MAXUSES_KEY, amount);
        }
    }

    public static boolean getDisplayUses(TagItem item) {
        return item.hasBooleanTag(ACTION_DISPLAYUSES_KEY);
    }

    public static void setDisplayUses(TagItem item, boolean value) {
        if (!value) { // default
            item.removeTag(ACTION_DISPLAYUSES_KEY);
        } else {
            item.setTag(ACTION_DISPLAYUSES_KEY, true);
        }
    }

    public static boolean getConsume(TagItem item) {
        return !item.hasBooleanTag(ACTION_CONSUME_AT_END_KEY);
    }

    public static void setConsume(TagItem item, boolean value) {
        if (value) { //default
            item.removeTag(ACTION_CONSUME_AT_END_KEY);
        } else {
            item.setTag(ACTION_CONSUME_AT_END_KEY, true);
        }
    }

    public static int getCooldownMs(TagItem item) {
        return item.getInteger(ACTION_COOLDOWN_KEY, 0);
    }

    public static void setCooldownMs(TagItem item, int amount) {
        if (amount <= 0) { // default
            item.removeTag(ACTION_COOLDOWN_KEY);
        } else {
            item.setTag(ACTION_COOLDOWN_KEY, amount);
        }
    }

    public static String getCooldownId(TagItem item) {
        return item.getString(ACTION_COOLDOWN_ID_KEY, DEFAULT_COOLDOWN_ID);
    }

    public static void setCooldownId(TagItem item, String value) {
        if (value == null || value.isEmpty() || value.equalsIgnoreCase(DEFAULT_COOLDOWN_ID)) { // default
            item.removeTag(ACTION_COOLDOWN_ID_KEY);
        } else {
            item.setTag(ACTION_COOLDOWN_ID_KEY, value.toLowerCase(Locale.ENGLISH));
        }
    }

    public static String getPermission(TagItem item) {
        return item.getString(ACTION_PERMISSION_KEY, null);
    }

    public static void setPermission(TagItem item, String value) {
        if (value == null || value.isEmpty()) {// default
            item.removeTag(ACTION_PERMISSION_KEY);
        } else {
            item.setTag(ACTION_PERMISSION_KEY, value.toLowerCase(Locale.ENGLISH));
        }
    }

    public static String getDefaultCooldownId() {
        return DEFAULT_COOLDOWN_ID;
    }

    public static void setVisualCooldown(TagItem item, boolean value) {
        if (value) { //default
            item.setTag(ACTION_VISUAL_COOLDOWN, true);
        } else {
            item.removeTag(ACTION_VISUAL_COOLDOWN);
        }
    }

    public static boolean getVisualCooldown(TagItem item) {
        return item.hasBooleanTag(ACTION_VISUAL_COOLDOWN);
    }

    public static void updateUsesDisplay(ItemStack item) {
        TagItem tagItem = ItemTag.getTagItem(item);
        boolean show = ActionsUtility.getDisplayUses(tagItem);
        Map<String, Object> metaMap = new LinkedHashMap<>(ItemUtils.getMeta(item).serialize());
        if (show && !metaMap.containsKey("lore")) {
            metaMap.put("lore", new ArrayList<String>());
        }

        if (metaMap.containsKey("lore")) {
            List<String> lore = new ArrayList<>((Collection<String>) metaMap.get("lore"));
            //that's a bit hardcoded!
            lore.removeIf((line) -> (line.startsWith(
                    "{\"italic\":false,\"color\":\"white\",\"translate\":\"item.durability\",\"with\":[{\"text\":\"")
                    && line.endsWith("\"}]}") || line.startsWith(
                    "{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"white\",\"text\":\"Durability:")
                    && line.endsWith("\"}],\"text\":\"\"}")));
            if (show) {
                int uses = ActionsUtility.getUses(tagItem);
                int maxUses = ActionsUtility.getMaxUses(tagItem);
                lore.add("{\"italic\":false,\"color\":\"white\",\"translate\":\"item.durability\",\"with\":[{\"text\":\"" +
                        (uses == -1 ? "∞" : uses) + "\"},{\"text\":\"" + (maxUses == -1 ? "∞" : maxUses) + "\"}]}");
            }
            if (!lore.isEmpty()) {
                metaMap.put("lore", lore);
            } else {
                metaMap.remove("lore");
            }
        }
        metaMap.put("==", "ItemMeta");
        item.setItemMeta((ItemMeta) ConfigurationSerialization.deserializeObject(metaMap));
    }
}

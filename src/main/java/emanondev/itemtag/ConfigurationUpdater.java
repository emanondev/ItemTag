package emanondev.itemtag;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.YMLConfig;
import emanondev.itemedit.storage.ServerStorage;
import emanondev.itemtag.command.itemtag.Actions;
import emanondev.itemtag.command.itemtag.SecurityUtil;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class ConfigurationUpdater {
    private static final int CURRENT_VERSION = 5;

    static void update() {
        ItemTag plugin = ItemTag.get();
        int version = plugin.getConfig().getInt("config-version", 1);
        if (version >= CURRENT_VERSION)
            return;

        if (version <= 1) {
            YMLConfig lang = ItemTag.get().getLanguageConfig(null);
            lang.set("gui.flags.entityfood", Arrays.asList(
                    "&6Sets whether the item can be used as animal's food",
                    "&bValue: &e%value%", "",
                    "&7If set to false deny vanilla breed behavior",
                    "&7 and also baby fast grow behavior",
                    "&7 on this item", "",
                    "&7[&fClick&7]&b Toggle"));
            lang.set("itemtag.flag.entityfood.feedback.standard",
                    "&9[&fItemTag&9] &aThe item can be used as animal's food.");
            lang.set("itemtag.flag.entityfood.feedback.custom",
                    "&9[&fItemTag&9] &aThe item can''t be used as animal's food.");
        }
        if (version <= 2) {
            YMLConfig lang = ItemTag.get().getLanguageConfig(null);
            lang.set("gui.middleclick.creative", "Middle Click");
            lang.set("gui.middleclick.other", "Press 1");
            if (Arrays.asList("&6&lNext Page", "", "&7[&fClick&7]&b Go to page &e%target_page%")
                    .equals(lang.get("gui.previous-page")))
                lang.set("gui.previous-page", Arrays.asList("&6&lPrevious Page", "",
                        "&7[&fClick&7]&b Go to page &e%target_page%"));
            if (Arrays.asList("&6&lPrevious Page", "", "&7[&fClick&7]&b Go to page &e%target_page%")
                    .equals(lang.get("gui.next-page")))
                lang.set("gui.previous-page", Arrays.asList("&6&lNext Page", "",
                        "&7[&fClick&7]&b Go to page &e%target_page%"));
            lang.set("gui.effects.potion", Arrays.asList("&6&l%effect%", "",
                    "&bLevel: &e%level% &7[Left/Right Click Change]",
                    "&bParticles: &e%particles% &7[ShiftLeft Click Toggle]",
                    "&bAmbient: &e%ambient% &7[ShiftRight Click Toggle]",
                    "&bIcon: &e%icon% &7[%middle_click% Toggle]", "&bDuration: &e%duration%"));
        }
        if (version <= 3) {
            YMLConfig lang = ItemTag.get().getLanguageConfig(null);
            lang.set("gui.flags.vanishcurse", Arrays.asList("&6Sets whether the item disappear on death",
                    "&bValue: &e%value%", "", "&7Works exactly as the enchantment Vanish Curse",
                    "&7But it's not an enchantment", "", "&7[&fClick&7]&b Toggle"));
        }
        if (version <= 4) {
            ServerStorage storage = ItemEdit.get().getServerStorage();
            ItemTag.get().log("Updating storage");
            for (String id : storage.getIds()) {
                ItemStack item = storage.getItem(id);
                TagItem tagItem = ItemTag.getTagItem(item);
                if (!Actions.hasActions(tagItem))
                    continue;
                List<String> actions = new ArrayList<>(Actions.getActions(tagItem));
                boolean updating = false;
                for (int i = 0; i < actions.size(); i++) {
                    String action = actions.get(i);
                    String prefix = null;
                    if (action.startsWith("commandasop%%:%%")) {
                        prefix = "commandasop%%:%%";
                    } else if (action.startsWith("servercommand%%:%%")) {
                        prefix = "servercommand%%:%%";
                    }
                    if (prefix == null)
                        continue;
                    actions.set(i, prefix + "-pin" +
                            SecurityUtil.generateControlKey(action.substring(prefix.length())) + " " + action.substring(prefix.length()));
                    updating = true;
                }
                if (updating) {
                    ItemTag.get().log("Updated item &e" + id);
                    Actions.setActions(tagItem, actions);
                    storage.setItem(id, tagItem.getItem());
                }
                ItemTag.get().log("&cWARNING");
                ItemTag.get().log("A severe security bug was patched, items from (/serveritem or /si)");
                ItemTag.get().log("have been updated to match security standards, however items inside");
                ItemTag.get().log("players inventories haven't been updated and may stop working if they");
                ItemTag.get().log("had any actions of servercommand or commandasop kind");
                ItemTag.get().log("If you need more info feel free to ask for support on our discord");
                ItemTag.get().log("Discord: https://discord.gg/w5HVCDPtRp");
            }
        }


        plugin.log("Updating configuration version (" + version + " -> " + CURRENT_VERSION + ")");
        plugin.getConfig().set("config-version", CURRENT_VERSION);
        plugin.getConfig().save();
    }
}

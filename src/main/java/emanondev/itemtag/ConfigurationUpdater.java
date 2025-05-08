package emanondev.itemtag;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.YMLConfig;
import emanondev.itemedit.storage.ServerStorage;
import emanondev.itemtag.actions.ActionsUtility;
import emanondev.itemtag.command.itemtag.Actions;
import emanondev.itemtag.command.itemtag.SecurityUtil;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

class ConfigurationUpdater {
    private static final int CURRENT_VERSION = 6;

    static void update() {
        ItemTag plugin = ItemTag.get();
        int version = plugin.getConfig().getInt("config-version", 1);
        if (version >= CURRENT_VERSION)
            return;
        if (version <= 4) {
            ServerStorage storage = ItemEdit.get().getServerStorage();
            ItemTag.get().log("Updating storage");
            for (String id : storage.getIds()) {
                ItemStack item = storage.getItem(id);
                TagItem tagItem = ItemTag.getTagItem(item);
                if (!ActionsUtility.hasActions(tagItem))
                    continue;
                List<String> actions = new ArrayList<>(ActionsUtility.getActions(tagItem));
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
                    ActionsUtility.setActions(tagItem, actions);
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
        if (version <= 5) {
            YMLConfig config = ItemTag.get().getConfig();
            config.set("flag.vanishcurse.override_keepinventory", false);
            config.save();
        }


        plugin.log("Updating configuration version (" + version + " -> " + CURRENT_VERSION + ")");
        plugin.getConfig().set("config-version", CURRENT_VERSION);
        plugin.getConfig().save();
    }
}

package emanondev.itemtag;

import emanondev.itemedit.APlugin;
import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ReloadCommand;
import emanondev.itemedit.compability.Hooks;
import emanondev.itemedit.storage.ServerStorage;
import emanondev.itemedit.utility.VersionUtils;
import emanondev.itemtag.actions.*;
import emanondev.itemtag.activity.ActivityManager;
import emanondev.itemtag.activity.target.TargetManager;
import emanondev.itemtag.command.ItemTagCommand;
import emanondev.itemtag.command.ItemTagUpdateOldItem;
import emanondev.itemtag.command.itemtag.SecurityUtil;
import emanondev.itemtag.compability.Placeholders;
import emanondev.itemtag.equipmentchange.EquipmentChangeListener;
import emanondev.itemtag.equipmentchange.EquipmentChangeListenerBase;
import emanondev.itemtag.equipmentchange.EquipmentChangeListenerUpTo1_13;
import emanondev.itemtag.equipmentchange.EquipmentChangeListenerUpTo1_8;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ItemTag extends APlugin {

    private static ItemTag plugin = null;
    private static TagManager tagManager = null;
    private static boolean USE_NBTAPI;
    private static boolean USE_FOLIA;
    @Getter
    private EquipmentChangeListenerBase equipChangeListener;
    @Getter
    private TargetManager targetManager;

    public static ItemTag get() {
        return plugin;
    }

    public static TagItem getTagItem(@Nullable ItemStack item) {
        return USE_NBTAPI ? new NBTAPITagItem(item) : new SpigotTagItem(item);
    }

    public static boolean useFolia() {
        return USE_FOLIA;
    }

    @Deprecated
    public TagManager getTagManager() {
        return tagManager;
    }

    public void onLoad() {
        plugin = this;
        try {
            Class.forName("io.papermc.paper.threadedregions.scheduler.RegionScheduler");
            USE_FOLIA = true;
        } catch (ClassNotFoundException e) {
            USE_FOLIA = false;
        }
    }

    @Override
    public void enable() {
        try {
            initDataPersistance();
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error while enabling ItemTag, disabling it");
            e.printStackTrace();
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        //true Enable

        try {
            //register equipmentchange listener
            if (VersionUtils.isVersionUpTo(1, 8, 9)) {
                equipChangeListener = new EquipmentChangeListenerUpTo1_8();
            } else if (VersionUtils.isVersionUpTo(1, 12, 2)) {
                equipChangeListener = new EquipmentChangeListenerUpTo1_13();
            } else {
                equipChangeListener = new EquipmentChangeListener();
            }
            equipChangeListener.reload();

            //TODO new features
            /*targetManager = new TargetManager();
            targetManager.load();
            TriggerManager.load();
            ActionManager.load();
            ConditionManager.load();
            ActivityManager.reload();*/

            this.registerCommand(new ItemTagCommand(), Collections.singletonList("it"));
            new ReloadCommand(this).register();
            this.registerCommand("itemtagupdateolditem", new ItemTagUpdateOldItem(), null);


            ActionHandler.clearActions(); //required for plugman reload
            ActionHandler.registerAction(new DelayedAction());
            ActionHandler.registerAction(new PermissionAction());
            ActionHandler.registerAction(new PlayerCommandAction());
            ActionHandler.registerAction(new PlayerAsOpCommandAction());
            ActionHandler.registerAction(new ServerCommandAction());
            ActionHandler.registerAction(new SoundAction());
            ActionHandler.registerAction(new MessageAction());
            if (Hooks.isPAPIEnabled()) {
                try {
                    this.log("Hooking into PlaceholderApi");
                    new Placeholders().register();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error while enabling ItemTag, disabling it");
            e.printStackTrace();
            Bukkit.getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void reload() {
        equipChangeListener.reload();
        Aliases.reload();
        ActivityManager.reload();//TODO
        ItemTagCommand.get().reload();
    }

    @Override
    public void disable() {
    }

    @Override
    protected void updateConfigurations(int oldConfigVersion) {
        if (oldConfigVersion <= 4) {
            ServerStorage storage = ItemEdit.get().getServerStorage();
            log("Updating storage");
            for (String id : storage.getIds()) {
                ItemStack item = storage.getItem(id);
                TagItem tagItem = ItemTag.getTagItem(item);
                if (!ActionsUtility.hasActions(tagItem)) {
                    continue;
                }
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
                    if (prefix == null) {
                        continue;
                    }
                    actions.set(i, prefix + "-pin" +
                            SecurityUtil.generateControlKey(action.substring(prefix.length())) + " " + action.substring(prefix.length()));
                    updating = true;
                }
                if (updating) {
                    log("Updated item &e" + id);
                    ActionsUtility.setActions(tagItem, actions);
                    storage.setItem(id, tagItem.getItem());
                }
                log("&cWARNING");
                log("A severe security bug was patched, items from (/serveritem or /si)");
                log("have been updated to match security standards, however items inside");
                log("players inventories haven't been updated and may stop working if they");
                log("had any actions of servercommand or commandasop kind");
                log("If you need more info feel free to ask for support on our discord");
                log("Discord: https://discord.gg/w5HVCDPtRp");
            }
        }
        if (oldConfigVersion <= 5) {
            getConfig().set("flag.vanishcurse.override_keepinventory", false);
        }
    }

    private void initDataPersistance() throws Exception {
        switch (getConfig().getString("data.preference", "SPIGOT").toUpperCase(Locale.ENGLISH)) {
            case "NBTAPI":
                try {
                    initNBTAPI();
                } catch (Exception e) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "NBTAPI is selected as data.preference but it's not installed/working, " +
                            "if you wish to use NBTAPI get the plugin at www.spigotmc.org/resources/7939/");
                    initDefault();
                }
                return;
            case "SPIGOT":
                initDefault();
                return;
            default:
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + getConfig().getString("data.preference", "SPIGOT") + " is selected as data.preference but it's unknown");
                initDefault();
        }
    }

    private void initNBTAPI() throws Exception {
        new NBTAPITagItem(new ItemStack(Material.STONE));//force load NBTAPI classes or fails
        USE_NBTAPI = true;
        tagManager = new NBTAPITagManager();
        this.log("Data using NBTAPI");
    }

    private void initSpigotPersistentDataAPI() throws Exception {
        USE_NBTAPI = false;
        tagManager = new SpigotTagManager();
        this.log("Data using Spigot PersistentDataContainer");
    }

    private void initDefault() throws Exception {
        if (!VersionUtils.isVersionAfter(1, 14)) {
            try {
                initNBTAPI();
            } catch (Exception e) {
                String error = "NBTAPI is required on this server version check www.spigotmc.org/resources/7939/";
                this.enableWithError(error);
            }
        } else {
            initSpigotPersistentDataAPI();
        }
    }
}

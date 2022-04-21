package emanondev.itemtag;

import emanondev.itemedit.APlugin;
import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.UpdateChecker;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ReloadCommand;
import emanondev.itemtag.actions.*;
import emanondev.itemtag.command.ItemTagCommand;
import emanondev.itemtag.equipmentchange.EquipmentChangeListener;
import emanondev.itemtag.equipmentchange.EquipmentChangeListenerBase;
import emanondev.itemtag.equipmentchange.EquipmentChangeListenerUpTo1_13;
import emanondev.itemtag.equipmentchange.EquipmentChangeListenerUpTo1_8;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class ItemTag extends APlugin {
    private static ItemTag plugin = null;
    private static TagManager tagManager = null;
    private static boolean OLD_TAGS;
    private EquipmentChangeListenerBase equipChangeListener;

    @Deprecated
    public TagManager getTagManager() {
        return tagManager;
    }

    public static ItemTag get() {
        return plugin;
    }

    public static TagItem getTagItem(ItemStack item) {
        return OLD_TAGS ? new NBTAPITagItem(item) : new SpigotTagItem(item);
    }

    public void onLoad() {
        plugin = this;
    }

    private final static int PROJECT_ID = 89634;

    @Override
    public Integer getProjectId() {
        return PROJECT_ID;
    }

    @Override
    public void enable() {
//Check if can enable
        try {
            Class.forName("org.spigotmc.SpigotConfig");
        } catch (Throwable t) {
            TabExecutorError exec = new TabExecutorError(
                    ChatColor.RED + "CraftBukkit is not supported!!! use Spigot or Paper");
            for (String command : this.getDescription().getCommands().keySet())
                registerCommand(command, exec, null);
            return;
        }
        if (Bukkit.getServer().getBukkitVersion().startsWith("1.7.")) {
            TabExecutorError exec = new TabExecutorError(ChatColor.RED + "1.7.x is not supported!!! use 1.8+");
            for (String command : this.getDescription().getCommands().keySet())
                registerCommand(command, exec, null);
            return;
        }

        //true Enable
        try {
            new UpdateChecker(this, PROJECT_ID).logUpdates();

            //set tagapi
            if (ItemEdit.NMS_VERSION.startsWith("v1_8_R") || ItemEdit.NMS_VERSION.startsWith("v1_9_R")
                    || ItemEdit.NMS_VERSION.startsWith("v1_10_R") || ItemEdit.NMS_VERSION.startsWith("v1_11_R")
                    || ItemEdit.NMS_VERSION.startsWith("v1_12_R") || ItemEdit.NMS_VERSION.startsWith("v1_13_R"))
                try {
                    new NBTAPITagItem(new ItemStack(Material.STONE));//force load NBTAPI classes or fails
                    OLD_TAGS = true;
                    tagManager = new NBTAPITagManager();
                } catch (Exception e) {
                    ItemEdit.TabExecutorError exec = new ItemEdit.TabExecutorError(ChatColor.RED + "NBTAPI is required on this server version check www.spigotmc.org/resources/7939/");
                    for (String command : this.getDescription().getCommands().keySet())
                        registerCommand(command, exec, null);
                    return;
                }
            else {

                OLD_TAGS = false;
                tagManager = new SpigotTagManager();
            }

            //register equipmentchange listener
            if (Integer.parseInt(ItemEdit.NMS_VERSION.split("_")[1]) < 9)
                equipChangeListener = new EquipmentChangeListenerUpTo1_8();
            else if (Integer.parseInt(ItemEdit.NMS_VERSION.split("_")[1]) < 13)
                equipChangeListener = new EquipmentChangeListenerUpTo1_13();
            else
                equipChangeListener = new EquipmentChangeListener();
            equipChangeListener.reload();

            this.registerCommand(new ItemTagCommand(), Collections.singletonList("it"));
            new ReloadCommand(this).register();

            ActionHandler.clearActions(); //required for plugman reload
            ActionHandler.registerAction(new DelayedAction());
            ActionHandler.registerAction(new PermissionAction());
            ActionHandler.registerAction(new PlayerCommandAction());
            ActionHandler.registerAction(new PlayerAsOpCommandAction());
            ActionHandler.registerAction(new ServerCommandAction());
            ActionHandler.registerAction(new SoundAction());
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
        ItemTagCommand.get().reload();
    }

    @Override
    public void disable() {

    }
}

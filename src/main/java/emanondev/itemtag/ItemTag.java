package emanondev.itemtag;

import emanondev.itemedit.APlugin;
import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ReloadCommand;
import emanondev.itemedit.compability.Hooks;
import emanondev.itemtag.actions.*;
import emanondev.itemtag.command.ItemTagCommand;
import emanondev.itemtag.command.ItemTagUpdateOldItem;
import emanondev.itemtag.compability.PlaceHolders;
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

    public EquipmentChangeListenerBase getEquipChangeListener() {
        return equipChangeListener;
    }

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
        try {

            //set tagapi
            if (ItemEdit.GAME_VERSION <= 13)
                try {
                    new NBTAPITagItem(new ItemStack(Material.STONE));//force load NBTAPI classes or fails
                    OLD_TAGS = true;
                    tagManager = new NBTAPITagManager();
                } catch (Exception e) {
                    String error = "NBTAPI is required on this server version check www.spigotmc.org/resources/7939/";
                    //this.enableWithError(error);
                    ItemEdit.TabExecutorError exec = new ItemEdit.TabExecutorError(ChatColor.RED + error);
                    for (String command : this.getDescription().getCommands().keySet())
                        registerCommand(command, exec, null);
                    log(org.bukkit.ChatColor.RED + error);
                    return;
                }
            else {
                OLD_TAGS = false;
                tagManager = new SpigotTagManager();
            }
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error while enabling ItemTag, disabling it");
            e.printStackTrace();
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        ConfigurationUpdater.update();
        //true Enable

        try {
            //register equipmentchange listener
            if (ItemEdit.GAME_VERSION == 8)
                equipChangeListener = new EquipmentChangeListenerUpTo1_8();
            else if (ItemEdit.GAME_VERSION < 13)
                equipChangeListener = new EquipmentChangeListenerUpTo1_13();
            else
                equipChangeListener = new EquipmentChangeListener();
            equipChangeListener.reload();

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
            if (Hooks.isPAPIEnabled()) {
                try {
                    this.log("Hooking into PlaceholderApi");
                    new PlaceHolders().register();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
            registerMetrics(BSTATS_PLUGIN_ID);
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error while enabling ItemTag, disabling it");
            e.printStackTrace();
            Bukkit.getServer().getPluginManager().disablePlugin(this);
        }
    }

    private static final int BSTATS_PLUGIN_ID = 15077;

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

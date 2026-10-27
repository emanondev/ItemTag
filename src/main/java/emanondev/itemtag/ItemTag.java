package emanondev.itemtag;

import emanondev.itemedit.APlugin;
import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.aliases.AliasSet;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ReloadCommand;
import emanondev.itemedit.compability.Hooks;
import emanondev.itemedit.storage.ServerStorage;
import emanondev.itemtag.actions.*;
import emanondev.itemtag.activity.ActivityManager;
import emanondev.itemtag.activity.target.TargetManager;
import emanondev.itemtag.command.ItemTagCommand;
import emanondev.itemtag.command.ItemTagUpdateOldItem;
import emanondev.itemtag.command.itemtag.SecurityUtil;
import emanondev.itemtag.compability.Placeholders;
import emanondev.itemtag.equipmentchange.EquipmentChangeListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Slf4j
public class ItemTag extends APlugin {

    private static ItemTag plugin = null;
    @Getter
    private EquipmentChangeListener equipChangeListener;
    @Getter
    private TargetManager targetManager;

    public static ItemTag get() {
        return plugin;
    }

    public static TagItem getTagItem(@Nullable ItemStack item) {
        return new SpigotTagItem(item);
    }

    public void onLoad() {
        plugin = this;
    }

    @Override
    public void load() {

    }

    @Override
    public void enable() {

        try {
            //register equipmentchange listener
            equipChangeListener = new EquipmentChangeListener();

            equipChangeListener.reload();
            registerListener(equipChangeListener);

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
            ActionHandler.registerAction(new PotionEffectAction());
            ActionHandler.registerAction(new SummonAction());
            ActionHandler.registerAction(new ProjectileAction());
            ActionHandler.registerAction(new ThrowTntAction());
            Aliases.registerAliasType(() -> new AliasSet<Action>("itemtag_actions", this) {
                @Override
                public String getName(Action value) {
                    return value.getId();
                }

                @Override
                public Collection<Action> getValues() {
                    return ActionHandler.getActions();
                }
            }, true);
            if (Hooks.isPAPIEnabled()) {
                try {
                    this.log("Hooking into PlaceholderApi");
                    new Placeholders().register();
                } catch (Throwable t) {
                    log.warn("Error in ItemTag", t);
                }
            }
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error while enabling ItemTag, disabling it");
            log.warn("Error in ItemTag", e);
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
                    log("Updated item <yellow>" + id);
                    ActionsUtility.setActions(tagItem, actions);
                    storage.setItem(id, tagItem.getItem());
                }
                log("<red>WARNING");
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

}

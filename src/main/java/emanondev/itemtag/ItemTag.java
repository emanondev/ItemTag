package emanondev.itemtag;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import emanondev.itemedit.APlugin;
import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.UpdateChecker;
import emanondev.itemedit.command.AbstractCommand;
import emanondev.itemtag.actions.*;
import emanondev.itemtag.command.ItemTagCommand;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

public class ItemTag extends APlugin {
	private static ItemTag plugin = null;
	private static TagManager tagManager = null;

	public TagManager getTagManager() {
		return tagManager;
	}

	public static ItemTag get() {
		return plugin;
	}

	public void onLoad() {
		plugin = this;
	}

	private final static int PROJECT_ID = 89634;

	public void onEnable() {
		try {
			if (Class.forName("org.spigotmc.SpigotConfig")==null)
				throw new NullPointerException();
		} catch (Throwable t) {
			TabExecutor exec = new TabExecutor() {

				@Override
				public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
					return Collections.emptyList();
				}

				@Override
				public boolean onCommand(CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
					sender.sendMessage(ChatColor.RED + "CraftBukkit is not supported!!! use Spigot or Paper");
					return true;
				}

			};
			for (String command : this.getDescription().getCommands().keySet()) {
				getCommand(command).setExecutor(exec);
				getCommand(command).setTabCompleter(exec);
			}
			return;
		}
		if (Integer.parseInt(ItemEdit.NMS_VERSION.split("_")[1]) < 8) {

			TabExecutor exec = new TabExecutor() {

				@Override
				public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
					return Collections.emptyList();
				}

				@Override
				public boolean onCommand(CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
					sender.sendMessage(ChatColor.RED + "This Game Version is not supported!!! (1.8+ is required)");
					return true;
				}

			};
			getCommand("itemtag").setExecutor(exec);
			getCommand("itemtag").setTabCompleter(exec);
			getCommand("itemtag").setAliases(Collections.singletonList("it"));
			return;
		}
		try {
			new UpdateChecker(this, PROJECT_ID).logUpdates();
			if (ItemEdit.NMS_VERSION.startsWith("v1_8_R") || ItemEdit.NMS_VERSION.startsWith("v1_9_R")
					|| ItemEdit.NMS_VERSION.startsWith("v1_10_R") || ItemEdit.NMS_VERSION.startsWith("v1_11_R")
					|| ItemEdit.NMS_VERSION.startsWith("v1_12_R") || ItemEdit.NMS_VERSION.startsWith("v1_13_R"))
				try {
					tagManager = new NBTAPITagManager();
				} catch (Exception e) {
					TabExecutor exec = new TabExecutor() {

						@Override
						public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
							return Collections.emptyList();
						}

						@Override
						public boolean onCommand(CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
							sender.sendMessage(ChatColor.RED + "NBTAPI is required on this server version check www.spigotmc.org/resources/7939/");
							return true;
						}

					};
					getCommand("itemtag").setExecutor(exec);
					getCommand("itemtag").setTabCompleter(exec);
					getCommand("itemtag").setAliases(Collections.singletonList("it"));
					return;
				}
			else
				tagManager = new SpigotTagManager();
			AbstractCommand c = new ItemTagCommand();
			getCommand(c.getName()).setExecutor(c);
			getCommand(c.getName()).setTabCompleter(c);
			getCommand(c.getName()).setAliases(Collections.singletonList("it"));

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
}

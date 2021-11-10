package emanondev.itemtag.command;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.command.AbstractCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.command.itemtag.*;

public class ItemTagCommand extends AbstractCommand {
	public static ItemTagCommand instance;

	public static ItemTagCommand get() {
		return instance;
	}

	public ItemTagCommand() {
		super("itemtag", ItemTag.get());
		instance = this;
		if (Integer.parseInt(ItemEdit.NMS_VERSION.split("_")[1]) < 9)
			this.registerSubCommand(new EffectsVeryOld(this));
		else if (Integer.parseInt(ItemEdit.NMS_VERSION.split("_")[1]) < 13)
			this.registerSubCommand(new EffectsOld(this));
		else
			this.registerSubCommand(new Effects(this));
		this.registerSubCommand(new Actions(this));
		this.registerSubCommand(new ConsumeActions(this));
		this.registerSubCommand(new Placeable(this));
		if (ItemEdit.NMS_VERSION.startsWith("v1_8_R"))
			this.registerSubCommand(new UsableOld(this));
		else
			this.registerSubCommand(new Usable(this));
		this.registerSubCommand(new CraftRecipeIngredient(this));
		this.registerSubCommand(new Smelt(this));
		this.registerSubCommand(new FurnaceFuel(this));
		this.registerSubCommand(new Enchantable(this));
		if (Integer.parseInt(ItemEdit.NMS_VERSION.split("_")[1]) > 8)
			this.registerSubCommand(new Renamable(this));

		if (Integer.parseInt(ItemEdit.NMS_VERSION.split("_")[1]) > 13)
			this.registerSubCommand(new Grindable(this));
	}

	public void registerSubCommand(SubCmd cmd) {
		super.registerSubCommand(cmd);
		if (cmd instanceof ListenerSubCmd)
			Bukkit.getPluginManager().registerEvents((Listener) cmd, ItemTag.get());
	}

}

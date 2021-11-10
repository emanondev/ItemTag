package emanondev.itemtag.command.itemtag;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.itemedit.Util;
import emanondev.itemedit.UtilsString;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.command.ItemTagCommand;
import emanondev.itemtag.command.ListenerSubCmd;

public class Enchantable extends ListenerSubCmd {

	private final static String ENCHANTABLE_KEY = ItemTag.get().getName().toLowerCase()+":enchantable";

	public Enchantable(ItemTagCommand cmd) {
		super("enchantable", cmd, true, true);
	}

	@Override
	public void onCmd(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		ItemStack item = this.getItemInHand(p);
		try {
			if (args.length > 2)
				throw new IllegalArgumentException("Wrong param number");
			boolean val = args.length == 2 ? Aliases.BOOLEAN.convertAlias(args[1])
					: ItemTag.get().getTagManager().hasBooleanTag(ENCHANTABLE_KEY, item);
			ItemMeta meta;
			if (val) {
				meta = ItemTag.get().getTagManager().removeTag(ENCHANTABLE_KEY, item).getItemMeta();
				Util.sendMessage(p, UtilsString.fix(this.getConfString("feedback.valid"), p, true));
			} else {
				meta = ItemTag.get().getTagManager().setTag(ENCHANTABLE_KEY, item, false).getItemMeta();
				Util.sendMessage(p, UtilsString.fix(this.getConfString("feedback.invalid"), p, true));
			}
			item.setItemMeta(meta);
		} catch (Exception e) {
			onFail(p);
		}
	}

	@Override
	public List<String> complete(CommandSender sender, String[] args) {
		if (args.length == 2)
			return Util.complete(args[1], Aliases.BOOLEAN);
		return new ArrayList<String>();
	}

	@EventHandler
	public void event(EnchantItemEvent event) {
		if (ItemTag.get().getTagManager().hasBooleanTag(ENCHANTABLE_KEY, event.getItem()))
			event.setCancelled(true);
	}

}

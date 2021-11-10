package emanondev.itemtag.command.itemtag;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.itemedit.Util;
import emanondev.itemedit.UtilsString;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.command.ItemTagCommand;
import emanondev.itemtag.command.ListenerSubCmd;

public class CraftRecipeIngredient extends ListenerSubCmd {

	private final static String CRAFTING_INGREDIENT_KEY = ItemTag.get().getName().toLowerCase()+":craft_ingredient";

	public CraftRecipeIngredient(ItemTagCommand cmd) {
		super("recipeingredient", cmd, true, true);
	}

	@Override
	public void onCmd(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		ItemStack item = this.getItemInHand(p);
		try {
			if (args.length > 2)
				throw new IllegalArgumentException("Wrong param number");
			boolean placeableValue = args.length == 2 ? Aliases.BOOLEAN.convertAlias(args[1])
					: ItemTag.get().getTagManager().hasBooleanTag(CRAFTING_INGREDIENT_KEY, item);
			ItemMeta meta;
			if (placeableValue) {
				meta = ItemTag.get().getTagManager().removeTag(CRAFTING_INGREDIENT_KEY, item).getItemMeta();
				Util.sendMessage(p, UtilsString.fix(this.getConfString("feedback.valid"), p, true));
			} else {
				meta = ItemTag.get().getTagManager().setTag(CRAFTING_INGREDIENT_KEY, item, false).getItemMeta();
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
	public void event(CraftItemEvent event) {
		for (ItemStack item : event.getInventory().getMatrix())
			if (ItemTag.get().getTagManager().hasBooleanTag(CRAFTING_INGREDIENT_KEY, item)) {
				event.setCancelled(true);
				return;
			}
	}

}

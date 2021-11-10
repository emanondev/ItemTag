package emanondev.itemtag.command.itemtag;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import emanondev.itemedit.Util;
import emanondev.itemedit.UtilsString;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.command.ItemTagCommand;
import emanondev.itemtag.command.ListenerSubCmd;

public class Grindable extends ListenerSubCmd {

	private final static String GRINDABLE_KEY = ItemTag.get().getName().toLowerCase()+":grindable";

	public Grindable(ItemTagCommand cmd) {
		super("grindable", cmd, true, true);
	}

	@Override
	public void onCmd(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		ItemStack item = this.getItemInHand(p);
		try {
			if (args.length > 2)
				throw new IllegalArgumentException("Wrong param number");
			boolean placeableValue = args.length == 2 ? Aliases.BOOLEAN.convertAlias(args[1])
					: ItemTag.get().getTagManager().hasBooleanTag(GRINDABLE_KEY, item);
			ItemMeta meta;
			if (placeableValue) {
				meta = ItemTag.get().getTagManager().removeTag(GRINDABLE_KEY, item).getItemMeta();
				Util.sendMessage(p, UtilsString.fix(this.getConfString("feedback.valid"), p, true));
			} else {
				meta = ItemTag.get().getTagManager().setTag(GRINDABLE_KEY, item, false).getItemMeta();
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
	public void event(InventoryClickEvent event) {
		if (!(event.getView().getTopInventory() instanceof GrindstoneInventory))
			return;
		GrindstoneInventory grind = (GrindstoneInventory) event.getView().getTopInventory();
		boolean found = false;
		if (ItemTag.get().getTagManager().hasBooleanTag(GRINDABLE_KEY, grind.getItem(0))
				|| ItemTag.get().getTagManager().hasBooleanTag(GRINDABLE_KEY, grind.getItem(1)))
			found = true;
		if (!found)
			return;
		if (event.getSlotType() == SlotType.RESULT) {
			event.setCancelled(true);
			//TODO lascia glich visivo quando usi F
			return;
		}
	}

}

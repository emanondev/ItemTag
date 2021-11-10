package emanondev.itemtag.command.itemtag;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.itemedit.Util;
import emanondev.itemedit.UtilsString;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.command.ItemTagCommand;
import emanondev.itemtag.command.ListenerSubCmd;

public class Placeable extends ListenerSubCmd {

	private final static String PLACEABLE_KEY = ItemTag.get().getName().toLowerCase()+":placeable";

	public Placeable(ItemTagCommand cmd) {
		super("placeable", cmd, true, true);
	}

	@Override
	public void onCmd(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		ItemStack item = this.getItemInHand(p);
		try {
			if (args.length > 2)
				throw new IllegalArgumentException("Wrong param number");
			boolean placeableValue = args.length == 2 ? Aliases.BOOLEAN.convertAlias(args[1])
					: ItemTag.get().getTagManager().hasBooleanTag(PLACEABLE_KEY, item);
			ItemMeta meta;
			if (placeableValue) {
				meta = ItemTag.get().getTagManager().removeTag(PLACEABLE_KEY, item).getItemMeta();
				Util.sendMessage(p, UtilsString.fix(this.getConfString("feedback.placeable"), p, true));
			} else {
				meta = ItemTag.get().getTagManager().setTag(PLACEABLE_KEY, item, false).getItemMeta();
				Util.sendMessage(p, UtilsString.fix(this.getConfString("feedback.unplaceable"), p, true));
			}
			item.setItemMeta(meta);
			//p.updateInventory();

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

	@EventHandler(ignoreCancelled=true)
	private void event(BlockPlaceEvent event) {
		if (ItemTag.get().getTagManager().hasBooleanTag(PLACEABLE_KEY, event.getItemInHand())) {
			event.setCancelled(true);
			if (cooldownSeconds==0 || !getPlugin().getCooldownAPI().hasCooldown(event.getPlayer(), PLACEABLE_KEY.toString())) {
				Util.sendMessage(event.getPlayer(), UtilsString.fix(this.getConfString("feedback.failed-place-attemp"), event.getPlayer(), true));
				if (cooldownSeconds!=0)
					getPlugin().getCooldownAPI().setCooldownSeconds(event.getPlayer(), PLACEABLE_KEY.toString(), cooldownSeconds);
			}
		}
	}
	
	private long cooldownSeconds = Math.max(0,this.getConfLong(
			"feedback.failed-place-attemp-message-cooldown-seconds"));
	
	public void reload() {
		super.reload();
		cooldownSeconds = Math.max(0,this.getConfLong(
				"feedback.failed-place-attemp-message-cooldown-seconds"));
	}

}